package com.sketch.papertracingart.Setting;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sketch.papertracingart.R;


public class SettingActivity extends AppCompatActivity {

    private PackageInfo packageInfo;

    private String format;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        TextView textView1 = findViewById(R.id.privacy_text);
        TextView textView2 = findViewById(R.id.version_text);
        TextView textView3 = findViewById(R.id.share_text);
        TextView textView4 = findViewById(R.id.rate_text);
        TextView version = findViewById(R.id.text_version);
        View privacy = findViewById(R.id.privacy);
        View share = findViewById(R.id.share);
        View rate = findViewById(R.id.rate);


        Typeface typeface = Typeface.createFromAsset(getAssets(), "Inter-Medium.ttf");
        textView1.setTypeface(typeface);
        textView2.setTypeface(typeface);
        textView3.setTypeface(typeface);
        textView4.setTypeface(typeface);

        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName;
             format = String.format(getString(R.string.gp), packageInfo.packageName);
            version.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            version.setText("1.0.0");
            format = String.format(getString(R.string.gp), "com.sketch.papertracingart");
        }

        Log.d("----------------","---------format"+format);


        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SettingActivity.this, PrivacyActivity.class);
                startActivity(intent);

            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareString(format);

            }
        });


        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(format);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);


            }
        });

    }


    /**
     * 分享纯文本
     *
     * @param content 内容
     */
    public void shareString(String content) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, content);//分享的文本内容
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share to"));
    }

}