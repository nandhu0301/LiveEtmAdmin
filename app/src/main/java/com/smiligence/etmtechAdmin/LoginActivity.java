package com.smiligence.etmtechAdmin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.smiligence.etmtechAdmin.bean.UserDetails;
import com.smiligence.etmtechAdmin.common.CommonMethods;
import com.smiligence.etmtechAdmin.common.NetworkStateReceiver;
import com.smiligence.etmtechAdmin.common.TextUtils;

import java.util.Objects;

import static com.smiligence.etmtechAdmin.common.Constant.ADMIN;
import static com.smiligence.etmtechAdmin.common.Constant.INVALID_PHONENUMBER;
import static com.smiligence.etmtechAdmin.common.Constant.LOGIN_SUCCESS;
import static com.smiligence.etmtechAdmin.common.Constant.PHONE_NUM_COLUMN;
import static com.smiligence.etmtechAdmin.common.Constant.TEXT_BLANK;
import static com.smiligence.etmtechAdmin.common.Constant.USER_DETAILS_TABLE;
import static com.smiligence.etmtechAdmin.common.MessageConstant.INVALID_PASSWORD;
import static com.smiligence.etmtechAdmin.common.MessageConstant.LOGIN_UNSUCCESS;

public class LoginActivity extends AppCompatActivity  implements NetworkStateReceiver.NetworkStateReceiverListener{


    EditText username, password;
    private TextView createAccount, forgetPassword;
    Button login;
    ImageView viewPassword, hidePassword;
    //String variables
    public static String usernameStr;
    static String passwordStr;
    public static String productKey,email;
    public static String businessName;
    public static String role_name, passwordStrDB;
    SharedPreferences.Editor editor;
    //Database variables
    DatabaseReference logindatabase;
    String userToken;
    private NetworkStateReceiver networkStateReceiver;
    androidx.appcompat.app.AlertDialog alertDialog;
    ViewGroup viewGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_login );
        setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );

        //Database variable Definition
        logindatabase =CommonMethods.fetchFirebaseDatabaseReference ( USER_DETAILS_TABLE );
        //Textview Variable Definition
        createAccount = findViewById ( R.id.createAccTextView );
        forgetPassword = findViewById ( R.id.resetPwTextView );
        //Button variable Definition
        login = findViewById ( R.id.loginbutton );
        //Imageview Variable Definition
        viewPassword = findViewById ( R.id.LoginViewPassword );
        hidePassword = findViewById ( R.id.LoginHidePassword );
        //EditText Variable Definition
        username = findViewById ( R.id.UsernameEditText );
        password = findViewById ( R.id.passwordEditText );

        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();


        FirebaseMessaging.getInstance().subscribeToTopic("all");
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            userToken = Objects.requireNonNull(task.getResult());
                            Log.d("tooooo", "token:" + userToken);
                            return;
                        }
                    }
                });


        //Viewpassword Onclick Listener
        viewPassword.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                password.setTransformationMethod ( PasswordTransformationMethod.getInstance () );
                hidePassword.setVisibility ( View.VISIBLE );
                viewPassword.setVisibility ( View.INVISIBLE );
            }
        } );

        //HidePassword OnClick Listener
        hidePassword.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                password.setTransformationMethod ( HideReturnsTransformationMethod.getInstance () );
                hidePassword.setVisibility ( View.INVISIBLE );
                viewPassword.setVisibility ( View.VISIBLE );

            }
        } );

        login.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v)
            {
                usernameStr = username.getText ().toString ().trim ();
                passwordStr = password.getText ().toString ().trim ();
                try {
                    final String encrtptPassword = CommonMethods.encrypt ( passwordStr );
                    if (TextUtils.validateLoginForm ( usernameStr, passwordStr, username, password ) == true) {

                        final Query userNameQuery = logindatabase.orderByChild ( PHONE_NUM_COLUMN ).equalTo ( usernameStr );

                        userNameQuery.addListenerForSingleValueEvent ( new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for ( DataSnapshot snap : dataSnapshot.getChildren () ) {
                                        UserDetails loginUserDetailslist = snap.getValue ( UserDetails.class );
                                        productKey = loginUserDetailslist.getProductKey ();
                                        businessName = loginUserDetailslist.getBusinessName ();
                                        role_name = loginUserDetailslist.getRoleName ();
                                        email=loginUserDetailslist.getEmail_Id();
                                        passwordStrDB = loginUserDetailslist.getPassword ();
                                        if (passwordStrDB.equals ( encrtptPassword )) {
                                            if (role_name.equals ( ADMIN )) {
                                                SharedPreferences sharedPreferences = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
                                                editor = sharedPreferences.edit ();
                                                editor.putString ( "userName", usernameStr );
                                                editor.putString ( "businessNameStr", businessName );
                                                editor.putString ( "productkeyStr", productKey );
                                                editor.putString("email",email);

                                                editor.commit ();

                                                logindatabase.child(loginUserDetailslist.getUserId()).child("deviceId").setValue(userToken);


                                                Toast.makeText ( LoginActivity.this, LOGIN_SUCCESS, Toast.LENGTH_SHORT ).show ();
                                                username.setText ( TEXT_BLANK );
                                                password.setText ( TEXT_BLANK );
                                                Intent intent = new Intent( getApplicationContext (), DashBoardActivity.class );
                                                intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                                startActivity ( intent );
                                            } else {
                                                Toast.makeText ( LoginActivity.this, LOGIN_UNSUCCESS, Toast.LENGTH_SHORT ).show ();
                                            }
                                        } else {
                                            password.setError("Invalid Password");
                                            Toast.makeText ( LoginActivity.this, INVALID_PASSWORD, Toast.LENGTH_SHORT ).show ();
                                        }
                                    }


                                } else {
                                    username.setError("Invalid PhoneNumber ");
                                    Toast.makeText ( LoginActivity.this, INVALID_PHONENUMBER, Toast.LENGTH_SHORT ).show ();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText ( LoginActivity.this, LOGIN_UNSUCCESS, Toast.LENGTH_SHORT ).show ();
                            }
                        } );

                    }
                } catch (Exception e) {
                    e.printStackTrace ();
                }

            }
        } );

        createAccount.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        } );
        forgetPassword.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        } );
    }



    @Override
    protected void onPause() {
        startNetworkBroadcastReceiver(this);
        unregisterNetworkBroadcastReceiver(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        startNetworkBroadcastReceiver(this);
        registerNetworkBroadcastReceiver(this);
        super.onResume();
    }

    @Override
    public void networkAvailable() {
        alertDialog.dismiss();
    }

    @Override
    public void networkUnavailable() {
        if (!((Activity) LoginActivity.this).isFinishing()) {
            showCustomDialog();
        }
    }

    public void startNetworkBroadcastReceiver(Context currentContext) {
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener((NetworkStateReceiver.NetworkStateReceiverListener) currentContext);
        registerNetworkBroadcastReceiver(currentContext);
    }


    public void registerNetworkBroadcastReceiver(Context currentContext) {
        currentContext.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public void unregisterNetworkBroadcastReceiver(Context currentContext) {
        currentContext.unregisterReceiver(networkStateReceiver);
    }

    private void showCustomDialog() {

        alertDialog.setCancelable(false);
        alertDialog.show();

    }


}