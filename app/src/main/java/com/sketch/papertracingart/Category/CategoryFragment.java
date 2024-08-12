package com.sketch.papertracingart.Category;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.sketch.papertracingart.Utils.Names;
import com.sketch.papertracingart.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class CategoryFragment extends Fragment {

    private static final List<String> categories = Names.getAllDir();

    public CategoryFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        ViewPager2 viewPager = view.findViewById(R.id.main_image_view_pager);
        TabLayout tabLayout = view.findViewById(R.id.main_image_tab);

        CategoryViewPagerAdapter adapter = new CategoryViewPagerAdapter(this, categories);
        viewPager.setAdapter(adapter);


        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {

            View customView = LayoutInflater.from(requireContext()).inflate(R.layout.category_tab_custom, null);
            tab.setCustomView(customView);

            TextView textView = customView.findViewById(R.id.category_tab_custom_title);
            Typeface typeface = Typeface.createFromAsset(requireContext().getAssets(), "Inter-Medium.ttf");
            textView.setTypeface(typeface);
            String dir = categories.get(position);
            textView.setText(dir.substring(dir.lastIndexOf("_") + 1));


        }).attach();


        return view;
    }

}
