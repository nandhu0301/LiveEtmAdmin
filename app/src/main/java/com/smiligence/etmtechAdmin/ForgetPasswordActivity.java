package com.smiligence.etmtechAdmin;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
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
import com.smiligence.etmtechAdmin.common.TextUtils;

import static com.smiligence.etmtechAdmin.common.CommonMethods.encrypt;
import static com.smiligence.etmtechAdmin.common.Constant.TEXT_BLANK;
import static com.smiligence.etmtechAdmin.common.Constant.USER_DETAILS_TABLE;
import static com.smiligence.etmtechAdmin.common.MessageConstant.INVALID_PASSWORD_SPECIFICATION;
import static com.smiligence.etmtechAdmin.common.MessageConstant.REQUIRED_MSG;

public class ForgetPasswordActivity extends AppCompatActivity {

    DatabaseReference LoginDataRef;
    EditText usernameEditText;
    String userNameString;
    Button changePassword;
    TextView loginPageText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        usernameEditText = findViewById(R.id.usernameEditText);
        changePassword = findViewById(R.id.changePassword);
        loginPageText = findViewById(R.id.loginPageText);
        LoginDataRef = CommonMethods.fetchFirebaseDatabaseReference(USER_DETAILS_TABLE);
        disableAutofill();

        loginPageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNameString = usernameEditText.getText().toString().trim();
                if ("".equals(usernameEditText.getText().toString())) {
                    usernameEditText.setError(REQUIRED_MSG);
                    Toast.makeText(ForgetPasswordActivity.this, ""+REQUIRED_MSG, Toast.LENGTH_SHORT).show();
                } else {
                    Query userNameQuery = LoginDataRef.orderByChild("phoneNumber").equalTo(usernameEditText.getText().toString());

                    userNameQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() > 0) {
                                for (DataSnapshot userIdSnap : dataSnapshot.getChildren()) {
                                    UserDetails loginUserDetails = userIdSnap.getValue(UserDetails.class);



                                    androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(ForgetPasswordActivity.this);
                                    LayoutInflater inflater = getLayoutInflater();
                                    final View dialogView = inflater.inflate(R.layout.change_password_dialog, null);
                                    dialogBuilder.setView(dialogView);
                                    dialogBuilder.setCancelable(false);
                                    EditText changePasswordEdit = (EditText) dialogView.findViewById(R.id.changePasswordEdit);
                                    EditText confrimPasswordEdit = dialogView.findViewById(R.id.changeConfrimPasswordEdit);
                                    ImageView viewPasswordChangePassword = dialogView.findViewById(R.id.viewPasswordChangePassword);
                                    ImageView hidePasswordChangePassword = dialogView.findViewById(R.id.hidePasswordChangePassword);

                                    ImageView viewPasswordChangeConfrimPassword = dialogView.findViewById(R.id.viewPasswordChangeConfrimPassword);
                                    ImageView hidePasswordChangeConfrimPassword = dialogView.findViewById(R.id.hidePasswordChangeConfrimPassword);
                                    Button changePasswordButtonDialog = dialogView.findViewById(R.id.finalButton);
                                    ImageView cancel = dialogView.findViewById(R.id.cancelIcon);


                                    viewPasswordChangePassword.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            changePasswordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                            changePasswordEdit.setSelection(changePasswordEdit.getText().length());
                                            hidePasswordChangePassword.setVisibility(View.VISIBLE);
                                            viewPasswordChangePassword.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                    hidePasswordChangePassword.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            changePasswordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                                            changePasswordEdit.setSelection(changePasswordEdit.getText().length());
                                            hidePasswordChangePassword.setVisibility(View.INVISIBLE);
                                            viewPasswordChangePassword.setVisibility(View.VISIBLE);
                                        }
                                    });


                                    viewPasswordChangeConfrimPassword.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            confrimPasswordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                            confrimPasswordEdit.setSelection(confrimPasswordEdit.getText().length());
                                            hidePasswordChangeConfrimPassword.setVisibility(View.VISIBLE);
                                            viewPasswordChangeConfrimPassword.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                    hidePasswordChangeConfrimPassword.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            confrimPasswordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                                            confrimPasswordEdit.setSelection(confrimPasswordEdit.getText().length());
                                            hidePasswordChangeConfrimPassword.setVisibility(View.INVISIBLE);
                                            viewPasswordChangeConfrimPassword.setVisibility(View.VISIBLE);
                                        }
                                    });


                                    final androidx.appcompat.app.AlertDialog b = dialogBuilder.create();

                                    if (!((Activity) ForgetPasswordActivity.this).isFinishing()) {
                                        b.show();
                                    }

                                    cancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            b.dismiss();
                                        }
                                    });
                                    changePasswordButtonDialog.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            String passwordString = changePasswordEdit.getText().toString().trim();
                                            String confrimPasswordstring = confrimPasswordEdit.getText().toString().trim();

                                            if (passwordString.equalsIgnoreCase("")) {
                                                changePasswordEdit.setError(REQUIRED_MSG);

                                                return;
                                            } else if (!TextUtils.isValidPassword(passwordString)) {
                                                changePasswordEdit.setError(INVALID_PASSWORD_SPECIFICATION);
                                                return;

                                            }
                                            else if (confrimPasswordstring.equalsIgnoreCase("")) {
                                                confrimPasswordEdit.setError(REQUIRED_MSG);
                                                return;

                                            }
                                            else if (passwordString.equals(confrimPasswordstring)) {


                                                try {
                                                    String encryptedpassword = encrypt(passwordString);
                                                    String encryptedConfirmPassword = encrypt(confrimPasswordstring);

                                                    if (!((Activity) ForgetPasswordActivity.this).isFinishing()) {
                                                        DatabaseReference passwordRef = LoginDataRef.child(loginUserDetails.getUserId());
                                                        passwordRef.child("password").setValue(encryptedpassword);
                                                        passwordRef.child("confirmPassword").setValue(encryptedConfirmPassword);


                                                        Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(intent);
                                                        b.dismiss();
                                                    }


                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            } else {
                                                confrimPasswordEdit.setText(TEXT_BLANK);
                                                Toast.makeText(ForgetPasswordActivity.this, "Enter Valid Password", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });


                                }


                            } else {
                                Toast.makeText(ForgetPasswordActivity.this, "Please enter registered User Name", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });


    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void disableAutofill() {
        getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
    }
}