package com.sketch.papertracingart.Setting;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.sketch.papertracingart.R;


public class PrivacyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_privacy);

        WebView webView = findViewById(R.id.webview);
        ImageView back = findViewById(R.id.btn_back);
        // 设置WebView的客户端
        webView.setWebViewClient(new WebViewClient());

        back.setOnClickListener(v -> finish());

        webView.loadUrl("file:///android_asset/privacy.html");

    }
}