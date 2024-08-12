package com.sketch.papertracingart.Category;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sketch.papertracingart.R;
import com.sketch.papertracingart.Utils.ItemDecoration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageFragment extends Fragment {

    private static final String ARG_CATEGORY = "category";
    private String category;
    private RecyclerView recyclerView;
    private ImageRecyclerViewAdapter adapter;

    public ImageFragment() {
        // 必须有一个空的构造函数
    }

    public static ImageFragment newInstance(String category) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString(ARG_CATEGORY);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        List<String> imagePaths = getImagePaths(category);
        adapter = new ImageRecyclerViewAdapter(imagePaths,requireActivity());
        recyclerView.setAdapter(adapter);
        ItemDecoration itemDecoration = new ItemDecoration(12, 10, 9);
        recyclerView.addItemDecoration(itemDecoration);

        return view;
    }



    @Override
    public void onPause() {
        super.onPause();

        Log.d("-----------","-------------onPause------");
    }

    private List<String> getImagePaths(String category) {
        List<String> imagePaths = new ArrayList<>();
        try {
            String[] files = requireContext().getAssets().list(category);
            if (files != null) {
                for (String file : files) {
                    imagePaths.add(category + "/" + file);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imagePaths;
    }
}

