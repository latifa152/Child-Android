package com.example.childproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.childproject.Common.Common;
import com.example.childproject.Model.ChildPRofile;
import com.example.childproject.Model.ChildUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import io.paperdb.Paper;

/* Main activity is the activity when child is register
from parent application then can access this
activity to login child from this
application  */

public class MainActivity extends AppCompatActivity {
    Button btnSignIn, btnRegister;

    FirebaseAuth auth;

    FirebaseDatabase db;

    DatabaseReference users;

    RelativeLayout rootlayout;

    AlertDialog waitingDialog;

    TextView txt_forgot_pwd;

   // StorageReference storageReference;
   private ArrayList<String> images;
   // FirebaseStorage storage;
    int size;
    int temp;
    String parentUid;
    private static final int PICK_FROM_GALLERY = 1;
    ArrayList <HashMap<String, String>> al = new ArrayList <HashMap <String, String>>();
   // User user = new User(  );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        Paper.init( this );
        auth = FirebaseAuth.getInstance();

        db = FirebaseDatabase.getInstance();

        users = db.getReference( );

        btnSignIn = (Button) findViewById( R.id.btn_sign_in );
     //   btnRegister = (Button) findViewById( R.id.btn_Register );
        rootlayout = (RelativeLayout) findViewById( R.id.root_layout );
        txt_forgot_pwd = (TextView) findViewById( R.id.txt_forgot_password );



      //  storage = FirebaseStorage.getInstance();
       // storageReference = storage.getReference();
        waitingDialog = new SpotsDialog( MainActivity.this );

        txt_forgot_pwd.setOnTouchListener( new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showDialogForgotpwd();
                return false;
            }
        } );

      /*  btnRegister.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterDialog();
            }
        } );*/

        btnSignIn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showparentDialog();
            }
        } );

    }
    private void showparentDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this  );
        dialog.setTitle( "VERIFY FIRST" );
        dialog.setMessage("Please enter parent information for Verification ");

        LayoutInflater inflater= LayoutInflater.from(this);

        View login_layout=inflater.inflate(R.layout.layout_verify,null);

        final MaterialEditText edtEmail = login_layout.findViewById( R.id.edt_email );
        final MaterialEditText edtpassword = login_layout.findViewById( R.id.edt_password );


        dialog.setView( login_layout );

        dialog.setPositiveButton( "Verify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                btnSignIn.setEnabled( false );
                if (TextUtils.isEmpty( edtEmail.getText().toString() ))
                {
                    Toasty.error( MainActivity.this, "Please Enter Your Email address", Toast.LENGTH_LONG, true ).show();
                    btnSignIn.setEnabled( true );
                    return;
                }
                if (TextUtils.isEmpty( edtpassword.getText().toString() ))
                {
                    Toasty.error( MainActivity.this, "Please Enter Your Password", Toast.LENGTH_LONG, true ).show();
                    btnSignIn.setEnabled( true );
                    return;
                }

                if (edtpassword.getText().toString().length() < 6)
                {
                    Toasty.error( MainActivity.this, "Your Password is too Short", Toast.LENGTH_LONG, true ).show();
                    btnSignIn.setEnabled( true );
                    return;
                }
                if((!Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()))
                {                       Toasty.error( MainActivity.this, "Please enter a valid Email (youremail@gmail.com)", Toast.LENGTH_LONG, true ).show();
                    btnSignIn.setEnabled( true );
                    return;
                }
                //Login


                waitingDialog.show();


                auth.signInWithEmailAndPassword( edtEmail.getText().toString() ,edtpassword.getText().toString() )
                        .addOnSuccessListener( new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
 parentUid = FirebaseAuth.getInstance().getUid();
                                            waitingDialog.dismiss();
                                Paper.book().write( Common.parent_uid,parentUid.toString() );
                                            showLoginDialog();
                                                                           /* FirebaseDatabase.getInstance().getReference(Common.Parent_information_tb1)
                                        .child( "XmPAjZTF29VOGXJD0TMmv33rwTC2" )
                                        .child( Common.Child_user_tb1 )
                                        .child( FirebaseAuth.getInstance().getCurrentUser().getUid() )
                                        .addValueEventListener( new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Log.e( "error1",dataSnapshot.toString() );

                                                Common.childUser = dataSnapshot.getValue( ChildUser.class );

                                                Toast.makeText( MainActivity.this,dataSnapshot.toString(),Toast.LENGTH_LONG ).show();
                                                Paper.book().write( Common.user_field,edtEmail.getText().toString() );
                                                Paper.book().write( Common.pwd_field,edtpassword.getText().toString() );
                                                Intent intent = new Intent( MainActivity.this,Home.class );

                                                showMessage();


                                                waitingDialog.dismiss();
                                                Toasty.success(MainActivity.this, "Login SucessFully", Toast.LENGTH_SHORT, true).show();

                                                startActivity( intent );
                                                finish();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        } );*/




                            }
                        } )
                        .addOnFailureListener( new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                waitingDialog.dismiss();
                                Toasty.error( MainActivity.this, "Failed !!" + e.getMessage(), Toast.LENGTH_LONG, true ).show();
                                btnSignIn.setEnabled( true );
                            }
                        } );



            }
        });

        dialog.setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        } );





        dialog.show();

    }




    private void showDialogForgotpwd() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder( this );
        alertDialog.setTitle( "FORGOT PASSWORD" );
        alertDialog.setMessage( "Please enter your email Address" );

        LayoutInflater inflater = LayoutInflater.from( MainActivity.this );
        View forgot_psw_layout = inflater.inflate( R.layout.forgot_pwd,null );
        final MaterialEditText edtEmail = (MaterialEditText)forgot_psw_layout.findViewById( R.id.edt_email );
        alertDialog.setView( forgot_psw_layout );
        //set button

        alertDialog.setPositiveButton( "RESET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {

            String email = edtEmail.getText().toString().trim() ;
            if(email!=null && email.length()>0) {
                waitingDialog.show();
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialogInterface.dismiss();
                                waitingDialog.dismiss();
                                Toasty.success(MainActivity.this, "Reset password link has been sent", Toast.LENGTH_LONG, true).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialogInterface.dismiss();
                        waitingDialog.dismiss();
                        Toasty.error(MainActivity.this, " " + e.getMessage(), Toast.LENGTH_LONG, true).show();

                    }
                });
            }else{
                Toasty.error(getApplicationContext(),"Please enter email id",Toast.LENGTH_SHORT).show();
            }
            }
        } );
        alertDialog.setNegativeButton( "CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        } );
        alertDialog.show();
    }

    // uses this method to sign in to application
    private void showLoginDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this  );
        dialog.setTitle( "SIGN IN" );
        dialog.setMessage("Please Use Email To sign in");

        LayoutInflater inflater= LayoutInflater.from(this);

        View login_layout=inflater.inflate(R.layout.layout_signin,null);

        final MaterialEditText edtEmail = login_layout.findViewById( R.id.edt_email );
        final MaterialEditText edtpassword = login_layout.findViewById( R.id.edt_password );


        dialog.setView( login_layout );

        dialog.setPositiveButton( "SIGN IN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                btnSignIn.setEnabled( false );
                if (TextUtils.isEmpty( edtEmail.getText().toString() ))
                {
                    Toasty.error( MainActivity.this, "Please Enter Your Email address", Toast.LENGTH_LONG, true ).show();
                    btnSignIn.setEnabled( true );
                    return;
                }
                if (TextUtils.isEmpty( edtpassword.getText().toString() ))
                {
                    Toasty.error( MainActivity.this, "Please Enter Your Password", Toast.LENGTH_LONG, true ).show();
                    btnSignIn.setEnabled( true );
                    return;
                }

                if (edtpassword.getText().toString().length() < 6)
                {
                    Toasty.error( MainActivity.this, "Your Password is too Short", Toast.LENGTH_LONG, true ).show();
                    btnSignIn.setEnabled( true );
                    return;
                }
                if((!Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()))
                {                       Toasty.error( MainActivity.this, "Please enter a valid Email (youremail@gmail.com)", Toast.LENGTH_LONG, true ).show();
                    btnSignIn.setEnabled( true );
                    return;
                }
                //Login


                waitingDialog.show();


                auth.signInWithEmailAndPassword( edtEmail.getText().toString() ,edtpassword.getText().toString() )
                        .addOnSuccessListener( new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {



                                FirebaseDatabase.getInstance().getReference(Common.Parent_information_tb1)
                                        .child( parentUid )
                                        .child( Common.Child_user_tb1 )
                                        .child( FirebaseAuth.getInstance().getCurrentUser().getUid() )
                                        .addValueEventListener( new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Log.e( "error1",dataSnapshot.toString() );
                                               // Toast.makeText( MainActivity.this,FirebaseAuth.getInstance().getUid(),Toast.LENGTH_LONG ).show();
                                                Common.childUser = dataSnapshot.getValue( ChildUser.class );


                                                Paper.book().write( Common.user_field,edtEmail.getText().toString() );
                                                Paper.book().write( Common.pwd_field,edtpassword.getText().toString() );

                                              // Intent intent = new Intent( MainActivity.this,Home.class );
                                               showMessage();

                                                getProfile();
                                                waitingDialog.dismiss();
                                                Toasty.success(MainActivity.this, "Login SucessFully", Toast.LENGTH_SHORT, true).show();

                                           //    startActivity( intent );
                                            //    finish();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        } );




                            }
                        } )
                        .addOnFailureListener( new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                waitingDialog.dismiss();
                                Toasty.error( MainActivity.this, "Failed !!" + e.getMessage(), Toast.LENGTH_LONG, true ).show();
                                btnSignIn.setEnabled( true );
                            }
                        } );



            }
        });

        dialog.setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        } );





        dialog.show();

    }
    public void clearFB(){

        DatabaseReference   users = db.getReference( Common.Child_information_tb1);
        users.child(  FirebaseAuth.getInstance().getUid() )
                // .child( "PhoneBook" )
                .removeValue()
                .addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                       // Toast.makeText(getApplicationContext(), "abc",Toast.LENGTH_LONG ).show();
                    }
                } )
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                      //  Toasty.error( getApplicationContext(), "Failed ! " + e.getMessage(), Toast.LENGTH_LONG, true ).show();

                    }
                } );
    }

    public void getProfile(){
        clearFB();
        DatabaseReference riderInformation = FirebaseDatabase.getInstance().getReference( );
        riderInformation
                .child( Common.Child_user_tb1)
                .child( FirebaseAuth.getInstance().getCurrentUser().getUid() ).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ChildPRofile obj = dataSnapshot.getValue( ChildPRofile.class );

                try {
                    Common.childUser.setcName(obj.getName());
                    Common.childUser.setcAvatarUrl(obj.getAvatarUrl());
                    Common.childUser.setcPhone(obj.getPhone());
                }catch (Exception e){
                    e.printStackTrace();
                    //System.out.println(e.printStackTrace());
                }
                Intent intent = new Intent(MainActivity.this, Home.class);

                Toasty.success(MainActivity.this, "Login SucessFully", Toast.LENGTH_SHORT, true).show();
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

   /* private void showRegisterDialog() {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this  );
        dialog.setTitle( "REGISTER" );
        dialog.setMessage("Please Use Email To Register");

        LayoutInflater inflater= LayoutInflater.from(this);

        View register_layout=inflater.inflate(R.layout.layout_register,null);

        final MaterialEditText edtEmail = register_layout.findViewById( R.id.edt_email );
        final MaterialEditText edtpassword = register_layout.findViewById( R.id.edt_password );
        final MaterialEditText edtName = register_layout.findViewById( R.id.edt_name );
        final MaterialEditText edtPhone = register_layout.findViewById( R.id.edt_phone );



        dialog.setView( register_layout );


        dialog.setPositiveButton( "Register", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                if(TextUtils.isEmpty( edtEmail.getText().toString() ))
                {
                    Toasty.error(MainActivity.this, "Please Enter Your Email address", Toast.LENGTH_LONG, true).show();

                    return; }
                if(TextUtils.isEmpty( edtpassword.getText().toString() ))
                {
                    Toasty.error(MainActivity.this, "Please Enter Your Password", Toast.LENGTH_LONG, true).show();
                    return;
                }
                if(TextUtils.isEmpty( edtName.getText().toString() ))
                {
                    Toasty.error(MainActivity.this, "Please Enter Your Name", Toast.LENGTH_LONG, true).show();
                    return;
                }
                if(TextUtils.isEmpty( edtPhone.getText().toString() ))
                {
                    Toasty.error(MainActivity.this, "Please Enter Your Phone Number", Toast.LENGTH_LONG, true).show();
                    return;
                }
                if((!Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()))
                {                       Toasty.error( MainActivity.this, "Please enter a valid Email (youremail@gmail.com)", Toast.LENGTH_LONG, true ).show();

                    return;
                }

                if(edtpassword.getText().toString().length()<6)
                {
                    Toasty.error(MainActivity.this, "Your Password is too Short", Toast.LENGTH_LONG, true).show();
                    return;

                }

                // Register User

                waitingDialog.show();
                auth.createUserWithEmailAndPassword( edtEmail.getText().toString(),edtpassword.getText().toString() )
                        .addOnSuccessListener( new OnSuccessListener <AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                // save user to db


                                user.setEmail( edtEmail.getText().toString());
                                user.setName(edtName.getText().toString());
                                user.setPassword( edtpassword.getText().toString() );
                                user.setPhone( (edtPhone.getText().toString()) );
                                user.setAvatarUrl("");
                                user.setKey(FirebaseAuth.getInstance( ).getCurrentUser().getUid());

                                users.child( Common.Parent_information_tb1 )
                                        .child( "UaeeM5NtvaSa9ZGMJt2L3AoLfzv1" )
                                        .child( Common.Child_information_tb1 )
                                        .child( FirebaseAuth.getInstance( ).getCurrentUser().getUid())
                                        .setValue( user )
                                        .addOnSuccessListener( new OnSuccessListener <Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                             //   loadallContact();
                                                waitingDialog.dismiss();
                                                Toasty.success(MainActivity.this, "Register SucessFully", Toast.LENGTH_SHORT, true).show();

                                            }
                                        } )
                                        .addOnFailureListener( new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                waitingDialog.dismiss();
                                                Toasty.error(MainActivity.this, "Failed ! "+ e.getMessage(), Toast.LENGTH_LONG, true).show();

                                            }
                                        } );


                            }
                        } )

                        .addOnFailureListener( new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                waitingDialog.dismiss();
                                Toasty.error(MainActivity.this, "Failed ! "+ e.getMessage(), Toast.LENGTH_LONG, true).show();

                            }
                        } );

            }
        } );

        dialog.setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        } );

        dialog.show();


    }*/
    private void showMessage() {
        if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED) {


            Intent messageService = new Intent( MainActivity.this,MessageService.class );
            startService( messageService );
            Toast.makeText(this, " message Sync started sucessfully ",Toast.LENGTH_LONG ).show();

        } else {
            // Todo : Then Set Permission
            final int REQUEST_CODE_ASK_PERMISSIONS = 123;
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
            Intent ImageService = new Intent( MainActivity.this,ImgesService.class );
            startService( ImageService );
            Toast.makeText(this, " message Sync started sucessfully ",Toast.LENGTH_LONG ).show();
        }
    }
}
