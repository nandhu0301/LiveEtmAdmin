package com.smiligence.etmtechAdmin.common;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.smiligence.etmtechAdmin.DashBoardActivity;
import com.smiligence.etmtechAdmin.LoginActivity;
import com.smiligence.etmtechAdmin.R;




public class SplashActivity extends AppCompatActivity {
    String userName;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_splash );

        new Handler().postDelayed (new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
                editor = sharedPreferences.edit ();
                userName = sharedPreferences.getString ( "userName", "" );

                if (!"".equals ( userName ))
                {
                    Intent intent = new Intent( SplashActivity.this, DashBoardActivity.class );
                    intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity ( intent );
                    finish ();
                }

                else if ("".equals ( userName ) || userName.equals ( null )) {
                    Intent intent = new Intent( SplashActivity.this, LoginActivity.class );
                    intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity ( intent );
                }
            }
        }, 1000 );
    }


}