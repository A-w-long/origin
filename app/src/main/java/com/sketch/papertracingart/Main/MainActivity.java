package com.sketch.papertracingart.Main;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.sketch.papertracingart.Setting.SettingActivity;
import com.sketch.papertracingart.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        ImageView setting = findViewById(R.id.setting);
        TextView title = findViewById(R.id.paper_traci);
        //设置字体
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Inter-Medium.ttf");
        title.setTypeface(typeface);


        viewPager2 = findViewById(R.id.main_view_pager);
        TabLayout tabLayout = findViewById(R.id.main_tab);

        MainViewPagerAdapter adapter = new MainViewPagerAdapter(this);
        viewPager2.setAdapter(adapter);

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        // 禁用滑动事件
        viewPager2.setUserInputEnabled(false); //true:滑动，false：禁止滑动


        //关联tabLayout和viewpager2，设置tabLayout标签样式
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            View customView = LayoutInflater.from(this).inflate(R.layout.main_tab_custom, null);
            tab.setCustomView(customView);

            View root = customView.findViewById(R.id.main_tab_root);
            ImageView tabIcon = customView.findViewById(R.id.main_custom_image);
            TextView textView = customView.findViewById(R.id.main_custom_text);
            //设置字体
            textView.setTypeface(typeface);

            if (tabIcon != null) {
                if (position == 0) {
                    tabIcon.setImageResource(R.drawable.main_select_draw);
                    textView.setText(getString(R.string.main_draw));
                    textView.setTextColor(ContextCompat.getColor(this, R.color.white));
                } else if (position == 1) {
                    tabIcon.setImageResource(R.drawable.unimport);
                    textView.setText(getString(R.string.import_photo));
                    textView.setTextColor(ContextCompat.getColor(this, R.color.main_title));
                    root.setBackgroundResource(R.drawable.main_tab_white);
                } else {
                    tabIcon.setImageResource(R.drawable.main_unselect_setting);
                    textView.setText(getString(R.string.favorite));
                    textView.setTextColor(ContextCompat.getColor(this, R.color.main_title));
                    root.setBackgroundResource(R.drawable.main_tab_white);
                }
            }
        }).attach();


        //设置标签选中事件
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View customView = tab.getCustomView();
                if (customView != null) {
                    View root = customView.findViewById(R.id.main_tab_root);
                    ImageView tabIcon = customView.findViewById(R.id.main_custom_image);
                    TextView textView = customView.findViewById(R.id.main_custom_text);
                    Typeface typeface = Typeface.createFromAsset(getAssets(), "Inter-Medium.ttf");
                    textView.setTypeface(typeface);
                    if (tabIcon != null) {
                        if (tab.getPosition() == 0) {
                            tabIcon.setImageResource(R.drawable.main_select_draw);
                            textView.setText(getString(R.string.main_draw));
                            textView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                            root.setBackgroundResource(R.drawable.main_tab);
                        } else if (tab.getPosition() == 1) {
                            tabIcon.setImageResource(R.drawable.import_photo);
                            textView.setText(getString(R.string.import_photo));
                            textView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                            root.setBackgroundResource(R.drawable.main_tab);
                        } else {
                            tabIcon.setImageResource(R.drawable.main_select_setting);
                            textView.setText(getString(R.string.favorite));
                            textView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                            root.setBackgroundResource(R.drawable.main_tab);
                        }
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View customView = tab.getCustomView();
                if (customView != null) {
                    View root = customView.findViewById(R.id.main_tab_root);
                    ImageView tabIcon = customView.findViewById(R.id.main_custom_image);
                    TextView textView = customView.findViewById(R.id.main_custom_text);
                    Typeface typeface = Typeface.createFromAsset(getAssets(), "Inter-Medium.ttf");
                    textView.setTypeface(typeface);
                    if (tabIcon != null) {
                        if (tab.getPosition() == 0) {
                            tabIcon.setImageResource(R.drawable.main_unselect_draw);
                            textView.setText(getString(R.string.main_draw));
                            textView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.main_title));
                            root.setBackgroundResource(R.drawable.main_tab_white);
                        } else if (tab.getPosition() == 1) {
                            tabIcon.setImageResource(R.drawable.unimport);
                            textView.setText(getString(R.string.import_photo));
                            textView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.main_title));
                            root.setBackgroundResource(R.drawable.main_tab_white);
                        } else {
                            tabIcon.setImageResource(R.drawable.main_unselect_setting);
                            textView.setText(getString(R.string.favorite));
                            textView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.main_title));
                            root.setBackgroundResource(R.drawable.main_tab_white);
                        }
                    }
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // 处理Tab重新选中事件
            }
        });


    }

}