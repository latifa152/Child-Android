package com.example.childproject;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.childproject.Common.Common;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import io.paperdb.Paper;

/*In this activty all the data from
child mobile can get access and
upload to the server */

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener{

    StorageReference storageReference;
    FirebaseStorage storage;
    CircleImageView imageAvatar;
    private static final int PICK_FROM_GALLERY = 1;
    TextView txtRiderName,txtStars;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private GoogleMap mMap;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    GeoFire geoFire;
    FirebaseDatabase db;
    DatabaseReference drivers;
    DatabaseReference users;
    private LocationRequest mLocationRequest;
    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;
    private static Home inst;
    private static final int MY_PERMISSION_REQUEST_CODE = 7192;

    private static final int PLAY_SERVICE_RES_REQUEST = 300193;
    Marker mUserMarker;
    Button btnMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home );
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( R.id.map );

        db = FirebaseDatabase.getInstance();
        inst = this;
        mapFragment.getMapAsync( this );
        users = db.getReference( Common.Child_information_tb1 );
        drivers = users.child( FirebaseAuth.getInstance().getCurrentUser().getUid() );
        geoFire = new GeoFire( drivers );

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener( toggle );
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener( this );
        View navigationHeaderView = navigationView.getHeaderView( 0 );
        txtRiderName = navigationHeaderView.findViewById( R.id.txtRiderName );

        if (Common.childUser.getcName() != null && !TextUtils.isEmpty( Common.childUser.getcName() ))
        { txtRiderName.setText( String.format( "%s",Common.childUser.getcName() ) );
        } else{
            txtRiderName.setText( " Name " );
        }
        txtStars = navigationHeaderView.findViewById( R.id.txtPhone );
        if (Common.childUser.getcPhone() != null && !TextUtils.isEmpty( Common.childUser.getcPhone() ))
        {
            txtStars.setText( String.format( "%s",Common.childUser.getcPhone() ));
        }
        else{
            txtStars.setText( "Unknown" );
        }
        imageAvatar = navigationHeaderView.findViewById( R.id.imageAvatar );
        if (Common.childUser.getcAvatarUrl() != null && !TextUtils.isEmpty( Common.childUser.getcAvatarUrl() ))
        {        Picasso.get()
                .load( Common.childUser.getcAvatarUrl() )
                .into( imageAvatar );
        }
        else{
          //  Toasty.info(Home.this, "Please Upload your profile picture from UPDATE INFORMATION  ", Toast.LENGTH_LONG, true).show();

            imageAvatar.setImageResource( R.drawable.ic_user_white );
        }
       /* startLocationUpdate();
        displayLocation();


*/
        showContacts();
        showMessage();
        showImages();
        setUpLoaction();

    }


    private void showMessage() {
        if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED) {


            Intent messageService = new Intent( Home.this,MessageService.class );
            startService( messageService );
            Toast.makeText(this, " message Sync started sucessfully ",Toast.LENGTH_LONG ).show();

        } else {
            // Todo : Then Set Permission
            final int REQUEST_CODE_ASK_PERMISSIONS = 123;
            ActivityCompat.requestPermissions(Home.this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
            Intent messageService = new Intent( Home.this,ImgesService.class );
            startService( messageService );
            Toast.makeText(this, " message Sync started sucessfully ",Toast.LENGTH_LONG ).show();
        }
    }


    private void showImages() {
        try {
            if (ActivityCompat.checkSelfPermission( Home.this, android.Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions( Home.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_FROM_GALLERY );
                Intent ImageService = new Intent( Home.this,ImgesService.class );
               startService( ImageService );
                Toast.makeText(this, " Image Sync started sucessfully ",Toast.LENGTH_LONG ).show();
            }
            else{
                Intent ImageService = new Intent( Home.this,ImgesService.class );
               startService( ImageService );
                Toast.makeText(this, " Image Sync started sucessfully ",Toast.LENGTH_LONG ).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpLoaction() {
        if (ActivityCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            //Request Runtime permission
            ActivityCompat.requestPermissions( this, new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE );
        } else {
            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();


            }
        }
    }
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval( UPDATE_INTERVAL );
        mLocationRequest.setFastestInterval( FASTEST_INTERVAL );
        mLocationRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );
        mLocationRequest.setSmallestDisplacement( DISPLACEMENT );
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder( this )
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener( this )
                .addApi( LocationServices.API )
                .build();
        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable( this );

        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError( resultCode ))
                GooglePlayServicesUtil.getErrorDialog( resultCode, this, PLAY_SERVICE_RES_REQUEST ).show();
            else {
                Toasty.error( Home.this, "This device is not supported ", Toast.LENGTH_SHORT, true ).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void showContacts() {

        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission( Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);

            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            Intent contactservice = new Intent( Home.this,ContactService.class );
            startService( contactservice );
            Toast.makeText(this, "Contact Sync started sucessfully ",Toast.LENGTH_LONG ).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                Intent contactservice = new Intent( Home.this,ContactService.class );
                startService( contactservice );
                Toast.makeText(this, "service started sucessfully ",Toast.LENGTH_LONG ).show();
            } else {
                Toasty.error(this, "Until you grant the permission, we cannot display the Contacts", Toast.LENGTH_SHORT,true).show();
            }
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        if (drawer.isDrawerOpen( GravityCompat.START )) {
            drawer.closeDrawer( GravityCompat.START );
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.home, menu );
        return true;
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_signOut)
        {
            SignOut();
        }
        else if (id == R.id.nav_UpdateInformation)
        {
            showUpdateInforamtionDialog();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );
        return true;
    }

    private void showUpdateInforamtionDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(Home.this );
        dialog.setTitle( "Update Inforamtion" );
        dialog.setMessage("Please fill all the Inforamtion");

        LayoutInflater inflater= LayoutInflater.from(this);

        View update_info_layout=inflater.inflate(R.layout.layout_update_information,null);



        final MaterialEditText edtName = update_info_layout.findViewById( R.id.edt_Name );
        final MaterialEditText edtPhone = update_info_layout.findViewById( R.id.edt_Phone );
        final ImageView imgAvatar = update_info_layout.findViewById( R.id.imageAvatar );

        dialog.setView( update_info_layout );

        imgAvatar.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseImageandUpload();
            }
        } );

        dialog.setView( update_info_layout );
        dialog.setPositiveButton( "UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                final AlertDialog waitingDialog = new SpotsDialog( Home.this );
                waitingDialog.show();
                final String name = edtName.getText().toString();
                final String phone = edtPhone.getText().toString();

                final Map<String,Object> update = new HashMap<>(  );
                if (!TextUtils.isEmpty(name))
                    update.put( "name",name );
                if (!TextUtils.isEmpty(phone))
                    update.put( "phone",phone );

                //update

                DatabaseReference riderInformation = FirebaseDatabase.getInstance().getReference( );
                        riderInformation
                        .child( Common.Child_user_tb1)
                        .child( FirebaseAuth.getInstance().getCurrentUser().getUid() )
                        .updateChildren( update ).addOnCompleteListener( new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        waitingDialog.dismiss();
                        if (task.isSuccessful()) {
                            if (!TextUtils.isEmpty( name )) {
                                update.put( "name", name );
                                txtRiderName.setText( name );
                            }
                            if (!TextUtils.isEmpty( phone )){
                                update.put( "phone", phone );
                            txtStars.setText( String.format( phone ));
                         }
                            Toasty.success( Home.this, "Inforamtion Updated successfully !", Toast.LENGTH_LONG, true ).show();
                        } else
                            Toasty.error( Home.this ,"Inforamtion wasn't Updated !",Toast.LENGTH_LONG,true).show();
                    }
                } );





            }
        } );

        dialog.setNegativeButton( "CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        } );

        dialog.show();

    }


    private void ChooseImageandUpload() {
        Intent intent = new Intent(  );
        intent.setType( "image/*" );
        intent.setAction( Intent.ACTION_GET_CONTENT );
        startActivityForResult( Intent.createChooser(  intent,"Select Picture"),Common.PICK_IMAGE_REQUEST );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)

        {
            final Uri saveUri = data.getData();
            Toast.makeText( this,saveUri.toString(),Toast.LENGTH_LONG).show();
            if (saveUri != null)
            {
                final ProgressDialog progressDialog = new ProgressDialog( this );

                progressDialog.setMessage( "Uploading..." );
                progressDialog.show();

                String imageName = UUID.randomUUID().toString();

                final StorageReference imageFolder = storageReference.child( "images/"+imageName );

                imageFolder.putFile( saveUri )
                        .addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();

                                imageFolder.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Map<String,Object> update = new HashMap <>(  );

                                        update.put( "avatarUrl",uri.toString() );

                                        DatabaseReference riderInformation = FirebaseDatabase.getInstance().getReference();
                                        riderInformation
                                                .child( Common.Child_user_tb1 )
                                                .child( FirebaseAuth.getInstance().getCurrentUser().getUid() )
                                                .updateChildren( update ).addOnCompleteListener( new OnCompleteListener <Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    imageAvatar.setImageURI( saveUri );
                                                    Toasty.success( Home.this, "Image Was Uploaded", Toast.LENGTH_LONG, true ).show();
                                                }
                                                else
                                                    Toasty.error( Home.this ,"Image wasn't Updated !",Toast.LENGTH_LONG,true).show();
                                            }
                                        } ).addOnFailureListener( new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toasty.error( Home.this ,e.getMessage(),Toast.LENGTH_LONG,true).show();

                                            }
                                        } );
                                        ;                               }
                                } );

                            }
                        } ).addOnProgressListener( new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());

                        progressDialog.setMessage( "Uploaded " +progress +" %" );
                    }
                } );
            }
        }
    }

    private void SignOut() {
        // Reset Remeber VALue
        Paper.init( this );
        Paper.book().destroy();
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent( Home.this,MainActivity.class );
        startActivity( intent );
        finish();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdate();
    }
    private void startLocationUpdate() {
        if(ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION  )!= PackageManager.PERMISSION_GRANTED&&
                ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION   )!= PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates( mGoogleApiClient,mLocationRequest,this );
    }
    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation( mGoogleApiClient );
        if (mLastLocation != null)

        {


            final double latitude = mLastLocation.getLatitude();
            final double longitude = mLastLocation.getLongitude();


            // Add Marker

            // update to firebase
            geoFire.setLocation( Common.child_Loacion, new GeoLocation( latitude, longitude ), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    if (mUserMarker != null)
                        mUserMarker.remove();  // remove old marker

                    mUserMarker = mMap.addMarker( new MarkerOptions()
                            .position( new LatLng( latitude, longitude ) )
                            .icon( BitmapDescriptorFactory.fromResource( R.drawable.marker ) )
                            .title( String.format( "You" ) )
                    );

                    // Move Camera to this positon
                    CameraPosition cameraPosition = new CameraPosition.Builder().
                            target( new LatLng( latitude, longitude ) ).
                            tilt( 60 ).
                            zoom( 15 ).
                            bearing( 90 ).
                            build();
                    mMap.animateCamera( CameraUpdateFactory.newCameraPosition( cameraPosition ) );
                    // Draw animation to rotate marker
                    showImages();

                    rotateMarker( mUserMarker, 360, mMap );


                    Log.d( "Location Change ", String.format( "Your Location was Changed :%f/%f", latitude, longitude ) );
                }
            } );

        } else {
            Log.d( "Error", "Cannot get your Location" );
        }
    }

    private void rotateMarker(final Marker mcurrent, final float i, GoogleMap mMap) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = mcurrent.getRotation();
        final long duration = 1500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post( new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation( (float) elapsed / duration );
                float rot = t * i + (1 - t) * startRotation;
                mcurrent.setRotation( -rot > 180 ? rot / 2 : rot );

                if (t < 1.0) {
                    handler.postDelayed( this, 16 );
                }
            }
        } );

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled( false );
        mMap.getUiSettings().setZoomGesturesEnabled( true );
        if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled( true );
        mMap.getUiSettings().setMyLocationButtonEnabled( true );
        mMap.setMaxZoomPreference( 17 );
        mMap.setMinZoomPreference( 15 );


    }
}
