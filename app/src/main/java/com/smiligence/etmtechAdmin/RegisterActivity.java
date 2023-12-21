package com.smiligence.etmtechAdmin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligence.etmtechAdmin.bean.UserDetails;
import com.smiligence.etmtechAdmin.common.CommonMethods;
import com.smiligence.etmtechAdmin.common.DateUtils;
import com.smiligence.etmtechAdmin.common.NetworkStateReceiver;
import com.smiligence.etmtechAdmin.common.TextUtils;

import java.util.ArrayList;
import java.util.Iterator;

import static com.smiligence.etmtechAdmin.common.Constant.ADMIN;
import static com.smiligence.etmtechAdmin.common.Constant.BOOLEAN_FALSE;
import static com.smiligence.etmtechAdmin.common.Constant.BOOLEAN_TRUE;
import static com.smiligence.etmtechAdmin.common.Constant.EMAIL_COLUMN;
import static com.smiligence.etmtechAdmin.common.Constant.EMAIL_EXIST;
import static com.smiligence.etmtechAdmin.common.Constant.INVALID_EMAIL;
import static com.smiligence.etmtechAdmin.common.Constant.INVALID_FIRSTNAME_SPECIFICATION;
import static com.smiligence.etmtechAdmin.common.Constant.INVALID_LASTNAME_SPECIFICATION;
import static com.smiligence.etmtechAdmin.common.Constant.INVALID_PASSWORD;
import static com.smiligence.etmtechAdmin.common.Constant.INVALID_PASSWORD_SPECIFICATION;
import static com.smiligence.etmtechAdmin.common.Constant.INVALID_PHONENUMBER;
import static com.smiligence.etmtechAdmin.common.Constant.INVALID_PRODUCT_KEy;
import static com.smiligence.etmtechAdmin.common.Constant.PASSWORD_LENGTH;
import static com.smiligence.etmtechAdmin.common.Constant.PASSWORD_LENGTH_TOO_SHORT;
import static com.smiligence.etmtechAdmin.common.Constant.PHONE_NUMBER_EXIST;
import static com.smiligence.etmtechAdmin.common.Constant.PHONE_NUM_COLUMN;
import static com.smiligence.etmtechAdmin.common.Constant.RESISTRATION_SUCCESS;
import static com.smiligence.etmtechAdmin.common.Constant.RE_ENTER_PASSWORD;
import static com.smiligence.etmtechAdmin.common.Constant.TEXT_BLANK;
import static com.smiligence.etmtechAdmin.common.Constant.USER_DETAILS_TABLE;
import static com.smiligence.etmtechAdmin.common.MessageConstant.REQUIRED_MSG;


public class RegisterActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener{

    private TextView login;
    EditText firstName, lastName, emailId, password, confirmPassword, phoneNumber, productKey;
    String firstnameStr, lastnameStr, emailIdStr, passwordStr, confirmPasswordStr, phoneNumberStr, productKeyStr;
    Button register;
    DatabaseReference loginUserDetails, productKeyDetails;
    ImageView viewPassword, hidePassword;
    ImageView viewConfirmPassword, hideConfirmPassword;
    ArrayList<String> productKeyArrayList = new ArrayList<>();
    ArrayList<String> businessNameArrayList = new ArrayList<>();
    boolean isvalidProductKey = BOOLEAN_FALSE;
    long maxid = 0;
    long roleDetailsMaxid = 0;
    public static String saved_productkey, saved_businessName;
    TextView textViewUsername;
    TextView textViewEmail;
    private NetworkStateReceiver networkStateReceiver;
    androidx.appcompat.app.AlertDialog alertDialog;
    ViewGroup viewGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_register );

        setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );

        login = findViewById ( R.id.loginReturnTextView );
        register = findViewById ( R.id.signUpButton );
        firstName = findViewById ( R.id.signUpFname );
        lastName = findViewById ( R.id.signUplastname );
        emailId = findViewById ( R.id.signUpEmailEditText );
        phoneNumber = findViewById ( R.id.SignupPhnumber );
        password = findViewById ( R.id.signUpPasswordEditText );
        confirmPassword = findViewById ( R.id.signUpConfirmPasswordEditText );
        productKey = findViewById ( R.id.SignupUniqueKey );
        viewPassword = findViewById ( R.id.ViewPassword );
        hidePassword = findViewById ( R.id.HidePassword );
        viewConfirmPassword = findViewById ( R.id.ViewConformPassword );
        hideConfirmPassword = findViewById ( R.id.HideConformPassword );

      //  productKeyArrayList.add ( "Favtech@1234" );
     //   businessNameArrayList.add ( "Favtech" );
        productKeyArrayList.add ("ETMtech@1234");
           businessNameArrayList.add ("ETMtech");


        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();

        loginUserDetails = CommonMethods.fetchFirebaseDatabaseReference(USER_DETAILS_TABLE );

        login.setOnClickListener (v -> {
            Intent intent = new Intent( RegisterActivity.this, LoginActivity.class );
            startActivity ( intent );
        });

        viewPassword.setOnClickListener (v -> {
            password.setTransformationMethod ( PasswordTransformationMethod.getInstance () );
            hidePassword.setVisibility ( View.VISIBLE );
            viewPassword.setVisibility ( View.INVISIBLE );
        });
        //Hide Password Onclick Listener
        hidePassword.setOnClickListener (v -> {
            password.setTransformationMethod ( HideReturnsTransformationMethod.getInstance () );
            hidePassword.setVisibility ( View.INVISIBLE );
            viewPassword.setVisibility ( View.VISIBLE );
        });
        //viewConfirm Password Onclick Listener
        viewConfirmPassword.setOnClickListener (v -> {
            confirmPassword.setTransformationMethod ( PasswordTransformationMethod.getInstance () );
            hideConfirmPassword.setVisibility ( View.VISIBLE );
            viewConfirmPassword.setVisibility ( View.INVISIBLE );
        });
        //HideConfirm Password OnClick Listener
        hideConfirmPassword.setOnClickListener (v -> {
            confirmPassword.setTransformationMethod ( HideReturnsTransformationMethod.getInstance () );
            hideConfirmPassword.setVisibility ( View.INVISIBLE );
            viewConfirmPassword.setVisibility ( View.VISIBLE );
        });


        //Register OnClick Listener
        register.setOnClickListener (v -> {
            firstnameStr = firstName.getText ().toString ().trim ();
            lastnameStr = lastName.getText ().toString ().trim ();
            emailIdStr = emailId.getText ().toString ().trim ();
            passwordStr = password.getText ().toString ().trim ();
            confirmPasswordStr = confirmPassword.getText ().toString ().trim ();
            phoneNumberStr = phoneNumber.getText ().toString ().trim ();
            productKeyStr = productKey.getText ().toString ().trim ();

            if ("".equals ( firstnameStr )) {
                firstName.setError ( REQUIRED_MSG );
                Toast.makeText(this, ""+REQUIRED_MSG, Toast.LENGTH_SHORT).show();
                return;
            } else if (!TextUtils.isValidlastName ( firstnameStr )) {
                firstName.setError ( INVALID_FIRSTNAME_SPECIFICATION );
                Toast.makeText(this, ""+INVALID_FIRSTNAME_SPECIFICATION, Toast.LENGTH_SHORT).show();
                return;
            } else if ("".equals ( lastnameStr )) {
                lastName.setError ( REQUIRED_MSG );
                Toast.makeText(this, ""+REQUIRED_MSG, Toast.LENGTH_SHORT).show();
                return;
            } else if (!TextUtils.isValidlastName ( lastnameStr )) {
                lastName.setError ( INVALID_LASTNAME_SPECIFICATION );
                Toast.makeText(this, ""+INVALID_LASTNAME_SPECIFICATION, Toast.LENGTH_SHORT).show();
                return;
            } else if ("".equals ( emailIdStr )) {
                emailId.setError ( REQUIRED_MSG );
                Toast.makeText(this, ""+REQUIRED_MSG, Toast.LENGTH_SHORT).show();
                return;
            } else if (!TextUtils.isValidEmail ( emailIdStr )) {
                emailId.setError ( INVALID_EMAIL );
                Toast.makeText(this, ""+INVALID_EMAIL, Toast.LENGTH_SHORT).show();
                return;
            } else if ("".equals ( phoneNumberStr )) {
                phoneNumber.setError ( REQUIRED_MSG );
                Toast.makeText(this, ""+REQUIRED_MSG, Toast.LENGTH_SHORT).show();
                return;
            } else if (!TextUtils.validatePhoneNumber ( phoneNumberStr )) {
                phoneNumber.setError ( INVALID_PHONENUMBER );
                Toast.makeText(this, ""+INVALID_PHONENUMBER, Toast.LENGTH_SHORT).show();
                return;
            } else if ("".equals ( passwordStr )) {
                password.setError ( REQUIRED_MSG );
                Toast.makeText(this, ""+REQUIRED_MSG, Toast.LENGTH_SHORT).show();
                return;
            } else if (!TextUtils.isValidPassword ( passwordStr )) {
                password.setError ( INVALID_PASSWORD_SPECIFICATION );
                Toast.makeText(this, ""+INVALID_PASSWORD_SPECIFICATION, Toast.LENGTH_SHORT).show();
                return;
            } else if ("".equals ( confirmPasswordStr )) {
                confirmPassword.setError ( REQUIRED_MSG );
                Toast.makeText(this, ""+REQUIRED_MSG, Toast.LENGTH_SHORT).show();
                return;
            } else if (passwordStr.equals ( confirmPasswordStr )
                    && (passwordStr.length () <PASSWORD_LENGTH)) {
                password.setError ( PASSWORD_LENGTH_TOO_SHORT );
                Toast.makeText(this, ""+PASSWORD_LENGTH_TOO_SHORT, Toast.LENGTH_SHORT).show();
                return;
            } else if (!passwordStr.equals ( confirmPasswordStr )) {
                confirmPassword.setText ( TEXT_BLANK );
                confirmPassword.setError ( RE_ENTER_PASSWORD );
                Toast.makeText(this, ""+RE_ENTER_PASSWORD, Toast.LENGTH_SHORT).show();
                return;
            } else if ("".equals ( productKeyStr )) {
                productKey.setError ( REQUIRED_MSG );
                Toast.makeText(this, ""+REQUIRED_MSG, Toast.LENGTH_SHORT).show();
                return;
            } else if (!"".equalsIgnoreCase ( productKeyStr )) {

                Iterator productKeyIterator = productKeyArrayList.iterator ();
                while (productKeyIterator.hasNext ()) {
                    String productKey = (String) productKeyIterator.next ();

                    if (productKeyStr.equalsIgnoreCase ( productKey )) {
                       // saved_businessName = businessNameArrayList.get ( productKeyArrayList.indexOf ( productKeyStr ) );
                        saved_businessName="ETMtech@1234";
                        isvalidProductKey = BOOLEAN_TRUE;
                        break;
                    } else {
                        isvalidProductKey = BOOLEAN_FALSE;
                    }
                }
            }
            if (!isvalidProductKey) {
            //    productKey.setText ( TEXT_BLANK );
                productKey.setError ( "Please enter valid ProductKey" );
                Toast.makeText ( RegisterActivity.this, INVALID_PRODUCT_KEy, Toast.LENGTH_SHORT ).show ();
            } else if (isvalidProductKey) {

                final Query PhonenumberQuery = loginUserDetails.orderByChild ( PHONE_NUM_COLUMN ).equalTo ( phoneNumberStr );
                final Query emailQuery = loginUserDetails.orderByChild ( EMAIL_COLUMN ).equalTo ( emailIdStr );

                PhonenumberQuery.addListenerForSingleValueEvent ( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getChildrenCount () > 0) {
                            Toast.makeText ( RegisterActivity.this, PHONE_NUMBER_EXIST, Toast.LENGTH_SHORT ).show ();
                        } else {

                            emailQuery.addListenerForSingleValueEvent ( new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot emailDataSnapshot) {

                                    if (emailDataSnapshot.getChildrenCount () > 0) {
                                        Toast.makeText ( RegisterActivity.this, EMAIL_EXIST, Toast.LENGTH_SHORT ).show ();
                                    } else {

                                        if ("".equals ( emailIdStr ) == false
                                                && TextUtils.isValidEmail ( emailIdStr )) {

                                            if ("".equals ( phoneNumberStr ) == false
                                                    && TextUtils.validatePhoneNumber ( phoneNumberStr )) {

                                                if (TextUtils.isValidPassword ( passwordStr )
                                                        && passwordStr.equals ( confirmPasswordStr )) {
                                                    if ("".equals ( productKeyStr ) == false) {
                                                        String createdDate = DateUtils.fetchCurrentDateAndTime ();
                                                        // For first time login Create Date and Last Login Date will be Same
                                                        String lastLoginedDate = createdDate;
                                                        try {
                                                            String encryptedpassword = CommonMethods.encrypt ( passwordStr );
                                                            String encryptedConfirmPassword = CommonMethods.encrypt ( confirmPasswordStr );
                                                            UserDetails loginDetails =
                                                                    new UserDetails ();

                                                            loginDetails.setUserId ( String.valueOf ( maxid + 1 ) );
                                                            loginDetails.setFirstName ( firstnameStr );
                                                            loginDetails.setLastName ( lastnameStr );
                                                            loginDetails.setEmail_Id ( emailIdStr );
                                                            loginDetails.setPassword ( encryptedpassword );
                                                            loginDetails.setConfirmPassword ( encryptedConfirmPassword );
                                                            loginDetails.setPhoneNumber ( phoneNumberStr );
                                                            loginDetails.setProductKey ( productKeyStr );
                                                            loginDetails.setCreationDate ( createdDate );
                                                            loginDetails.setLastLoginDate ( lastLoginedDate );
                                                            loginDetails.setBusinessName ( saved_businessName );
                                                            loginDetails.setRoleName (ADMIN );
                                                            loginDetails.setStoreName ( "" );

                                                            loginUserDetails.child ( String.valueOf ( maxid + 1 ) ).setValue ( loginDetails );

                                                            Toast.makeText ( RegisterActivity.this, RESISTRATION_SUCCESS, Toast.LENGTH_SHORT ).show ();
                                                            AlertDialog.Builder mBuilder = new AlertDialog.Builder ( RegisterActivity.this );
                                                            View mView = getLayoutInflater ().inflate ( R.layout.create_login_crendentials, null );
                                                            final TextView userName = mView.findViewById ( R.id.Usernamedialog );
                                                            final TextView loginpassword = mView.findViewById ( R.id.Passworddialog );

                                                            userName.setText ( "UserName:  " + phoneNumberStr );
                                                            loginpassword.setText ( "Password:  " + passwordStr );
                                                            mBuilder.setNegativeButton ( "OK", (dialogInterface, i) -> {
                                                                dialogInterface.dismiss();
                                                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                startActivity(intent);
                                                                firstName.setText(TEXT_BLANK);
                                                                lastName.setText(TEXT_BLANK);
                                                                emailId.setText(TEXT_BLANK);
                                                                phoneNumber.setText(TEXT_BLANK);
                                                                password.setText(TEXT_BLANK);
                                                                confirmPassword.setText(TEXT_BLANK);
                                                                productKey.setText(TEXT_BLANK);
                                                            });
                                                            mBuilder.setView ( mView );
                                                            final AlertDialog dialog = mBuilder.create ();
                                                            dialog.show ();
                                                            dialog.setCancelable ( BOOLEAN_FALSE );
                                                        } catch (Exception e) {
                                                            e.printStackTrace ();
                                                        }
                                                    } else {
                                                        Toast.makeText ( RegisterActivity.this, INVALID_PRODUCT_KEy, Toast.LENGTH_SHORT ).show ();
                                                        productKey.setText ( TEXT_BLANK );
                                                    }
                                                } else {
                                                    Toast.makeText ( getApplicationContext (), INVALID_PASSWORD, Toast.LENGTH_LONG ).show ();
                                                }
                                            } else {
                                                Toast.makeText ( getApplicationContext (), INVALID_PHONENUMBER, Toast.LENGTH_LONG ).show ();
                                            }
                                        } else {
                                            Toast.makeText ( getApplicationContext (), INVALID_EMAIL, Toast.LENGTH_LONG ).show ();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                } );
            }
        });

        loginUserDetails.addValueEventListener ( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists ()) {
                    maxid = (dataSnapshot.getChildrenCount ());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        } );


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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
        if (!((Activity) RegisterActivity.this).isFinishing()) {
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
