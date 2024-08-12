package com.sketch.papertracingart.Main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.sketch.papertracingart.Category.CategoryFragment;
import com.sketch.papertracingart.Favorite.FavoriteFragment;
import com.sketch.papertracingart.Import.ImportListFragment;


public class MainViewPagerAdapter extends FragmentStateAdapter {
    public MainViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        if (position == 0) {
            return new CategoryFragment();
        } else if(position==1){
            return new ImportListFragment();
        }else {
            return new FavoriteFragment();
        }

    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
