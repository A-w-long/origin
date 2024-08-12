package com.sketch.papertracingart.Favorite;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sketch.papertracingart.R;
import com.sketch.papertracingart.Room.AppDatabase;
import com.sketch.papertracingart.Room.FavoriteImage;
import com.sketch.papertracingart.Room.FavoriteImageDao;
import com.sketch.papertracingart.Utils.ItemDecoration;

import java.util.List;

public class FavoriteFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavoriteAdapter imageAdapter;
    private FavoriteImageDao favoriteImageDao;
    private ImageView back;
    private ImageView title;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        // 初始化 RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        back = view.findViewById(R.id.favorite_back);
        title = view.findViewById(R.id.favorite_title);

        // 初始化数据库和 DAO
        AppDatabase db = AppDatabase.getInstance(requireContext());
        favoriteImageDao = db.favoriteImageDao();

        // 初始化适配器
        imageAdapter = new FavoriteAdapter(requireContext(), this);
        recyclerView.setAdapter(imageAdapter);
        ItemDecoration itemDecoration = new ItemDecoration(12, 10, 9);
        recyclerView.addItemDecoration(itemDecoration);

        refreshData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    public void refreshData() {
        new Thread(() -> {
            List<FavoriteImage> favoriteImages = favoriteImageDao.getAll();
            requireActivity().runOnUiThread(() -> {
                if (favoriteImages.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                     back.setVisibility(View.VISIBLE); // Uncomment if you have a placeholder image
                     title.setVisibility(View.VISIBLE); // Uncomment if you have a placeholder image
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                     title.setVisibility(View.GONE); // Uncomment if you have a placeholder image
                     back.setVisibility(View.GONE); // Uncomment if you have a placeholder image
                }
                imageAdapter.updateData(favoriteImages);
            });
        }).start();
    }

}
