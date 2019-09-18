package com.example.childproject;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.childproject.Common.Common;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import es.dmoral.toasty.Toasty;

public class ContactService extends Service {
    int temp;
    DatabaseReference users;
    FirebaseDatabase db;
    ArrayList<HashMap<String, String>> al = new ArrayList <HashMap <String, String>>();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        loadallContact();
        return  START_STICKY;
    }

    private void loadallContact() {

        HashMap<String, String> nameNumberMap = new HashMap <String, String>();
        db = FirebaseDatabase.getInstance();
        users = db.getReference( Common.Child_information_tb1);
       /* Cursor phones = getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC" );
        temp = 0;
        while (phones.moveToNext()) {
            final String contactName = phones.getString( phones.getColumnIndex( ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME ) );
            String image = phones.getString( phones.getColumnIndex( ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI ) );
            String contactNumber = phones.getString( phones.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER ) );


            nameNumberMap.put( "Name", contactName );
            nameNumberMap.put( "Number", contactNumber );
            al.add( nameNumberMap );

           users.child( FirebaseAuth.getInstance().getCurrentUser().getUid() )
                    .child( "PhoneBook" )
                    .push()
                    .setValue( nameNumberMap )
                    .addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                           // Toast.makeText(ContactService.this, "abc",Toast.LENGTH_LONG ).show();
                        }
                    } )
                    .addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                            Toasty.error( ContactService.this, "Failed ! " + e.getMessage(), Toast.LENGTH_LONG, true ).show();

                        }
                    } );



        }


        Log.e( "check", "as2" );
        customAdapter adapter = new customAdapter( this, al );


        //  list.setAdapter(adapter);
        Log.e( "check", String.valueOf( al.size() ) );
        Log.e( "check", String.valueOf( nameNumberMap.toString() ) );*/
        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                //plus any other properties you wish to query
        };

        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, null);
        } catch (SecurityException e) {
            e.printStackTrace();
            //SecurityException can be thrown if we don't have the right permissions
        }

        if (cursor != null) {
            try {
                HashSet<String> normalizedNumbersAlreadyFound = new HashSet<>();
                int indexOfNormalizedNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER);
                int indexOfDisplayName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int indexOfDisplayNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                while (cursor.moveToNext()) {
                    String normalizedNumber = cursor.getString(indexOfNormalizedNumber);
                    if (normalizedNumbersAlreadyFound.add(normalizedNumber)) {
                        String displayName = cursor.getString(indexOfDisplayName);
                        String displayNumber = cursor.getString(indexOfDisplayNumber);
                        Log.e( "check", displayName );
                        nameNumberMap.put( "Name", displayName );
                        nameNumberMap.put( "Number", displayNumber );
                      //  al.add( nameNumberMap );

                        users.child( FirebaseAuth.getInstance().getCurrentUser().getUid() )
                                .child( "PhoneBook" )
                                .push()
                                .setValue( nameNumberMap )
                                .addOnSuccessListener( new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Toast.makeText(ContactService.this, "abc",Toast.LENGTH_LONG ).show();
                                    }
                                } )
                                .addOnFailureListener( new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                            Toasty.error( ContactService.this, "Failed ! " + e.getMessage(), Toast.LENGTH_LONG, true ).show();

                                    }
                                } );

                        //haven't seen this number yet: do something with this contact!
                    } else {
                        //don't do anything with this contact because we've already found this number
                    }
                }
            } finally {
                cursor.close();
            }
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
//player.stop();
    }
}
