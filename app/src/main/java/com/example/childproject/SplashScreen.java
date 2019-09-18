package com.example.childproject;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.childproject.Common.Common;
import com.example.childproject.Model.ChildPRofile;
import com.example.childproject.Model.ChildUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;
import io.paperdb.Paper;

/*splash screen activty contain the fuctionality of
checking the user that it is already login or not
 if its already login then bring it to home activity and it
  not bring to main activity */

public class SplashScreen extends AppCompatActivity {
    private  static int SPLASH_TIME_OUT = 3000;
    FirebaseAuth auth;
    String parentUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_splash_screen );
        Paper.init( this );
        FirebaseApp.initializeApp(this);

        final String user = Paper.book().read( Common.user_field );
        final String pwd = Paper.book().read( Common.pwd_field );
        parentUid = Paper.book().read( Common.parent_uid );


        auth = FirebaseAuth.getInstance();
        new Handler(  ).postDelayed( new Runnable() {
            @Override
            public void run() {
                if (user != null && pwd != null)
                {
                    if (!TextUtils.isEmpty(user )
                            && !TextUtils.isEmpty(pwd ))
                    {
                        autoLogin(user,pwd);
                    }
                }
                else
                {
                    Intent homeIntent = new Intent( SplashScreen.this,IntroActivity.class );  // introActivity
                    startActivity( homeIntent );
                    finish();
                }

            }
        },SPLASH_TIME_OUT );

    }
    private void autoLogin(String user, String pwd) {


        //Login

        auth.signInWithEmailAndPassword(user, pwd )
                .addOnSuccessListener( new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

        FirebaseDatabase.getInstance().getReference(Common.Parent_information_tb1)
                                .child(parentUid)
                                .child( Common.Child_user_tb1 )
                                .child( FirebaseAuth.getInstance().getCurrentUser().getUid() )
                                .addValueEventListener( new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                      //  Toasty.success( SplashScreen.this, dataSnapshot.toString(), Toast.LENGTH_SHORT, true ).show();

                                        Common.childUser = dataSnapshot.getValue( ChildUser.class );

                                        if(Common.childUser!=null) {

                                            getProfile();
                                        }else{
                                            Intent intent = new Intent(SplashScreen.this, IntroActivity.class);

                                            Toasty.success(SplashScreen.this, "Login SucessFully", Toast.LENGTH_SHORT, true).show();
                                            startActivity(intent);
                                            finish();
                                        }
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
                        Toasty.error( SplashScreen.this, "Failed !!" + e.getMessage(), Toast.LENGTH_LONG, true ).show();
                        Paper.init( SplashScreen.this );
                        Paper.book().destroy();
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent( SplashScreen.this, MainActivity.class );
                        startActivity( intent );
                        finish();
                    }
                } );
    }
    public void clearFB(){

        DatabaseReference   users = FirebaseDatabase.getInstance().getReference( Common.Child_information_tb1);
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

        DatabaseReference riderInformation = FirebaseDatabase.getInstance().getReference( );
        riderInformation
                .child( Common.Child_user_tb1)
                .child( FirebaseAuth.getInstance().getCurrentUser().getUid() ).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ChildPRofile obj = dataSnapshot.getValue( ChildPRofile.class );
                clearFB();
                try {
                    Common.childUser.setcName(obj.getName());
                    Common.childUser.setcAvatarUrl(obj.getAvatarUrl());
                    Common.childUser.setcPhone(obj.getPhone());
                }catch (Exception e){
                    e.printStackTrace();
                }
                Intent intent = new Intent(SplashScreen.this, Home.class);

                Toasty.success(SplashScreen.this, "Login SucessFully", Toast.LENGTH_SHORT, true).show();
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
