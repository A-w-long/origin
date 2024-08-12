package com.sketch.papertracingart.Category;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class CategoryViewPagerAdapter extends FragmentStateAdapter {

    private final List<String> categories;

    public CategoryViewPagerAdapter(@NonNull Fragment fragment, List<String> categories) {
        super(fragment);
        this.categories = categories;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return ImageFragment.newInstance(categories.get(position));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

}
