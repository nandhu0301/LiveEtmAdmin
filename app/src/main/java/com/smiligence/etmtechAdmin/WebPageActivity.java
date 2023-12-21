package com.smiligence.etmtechAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class WebPageActivity extends AppCompatActivity {

    WebView webView;

    String intentHtmlUrl;
    Button backArrow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_page);
        webView = findViewById(R.id.webView);
        backArrow = findViewById(R.id.backArrow);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToAddDescription = new Intent(getApplicationContext(), Add_Description_Activity.class);
                backToAddDescription.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(backToAddDescription);
            }
        });

        intentHtmlUrl = getIntent().getStringExtra("intentHtmlUrl");
        if (intentHtmlUrl != null) {
            webView.loadUrl(intentHtmlUrl);
        }

    }
}