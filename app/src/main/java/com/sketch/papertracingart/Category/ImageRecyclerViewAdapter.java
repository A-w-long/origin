package com.sketch.papertracingart.Category;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.sketch.papertracingart.Camera.CameraActivity;
import com.sketch.papertracingart.R;
import com.sketch.papertracingart.Room.AppDatabase;
import com.sketch.papertracingart.Room.FavoriteImage;
import com.sketch.papertracingart.Room.FavoriteImageDao;
import com.sketch.papertracingart.Utils.DiffUtilUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;
import java.util.Objects;

public class ImageRecyclerViewAdapter extends RecyclerView.Adapter<ImageRecyclerViewAdapter.ViewHolder> {
    private List<String> imagePaths;

    private List<String> imagePathsOld;
    private final FavoriteImageDao favoriteImageDao;
    private final Activity context;

    public ImageRecyclerViewAdapter(List<String> imagePaths, Activity context) {
        this.imagePaths = imagePaths;
        this.context = context;
        AppDatabase db = AppDatabase.getInstance(context);
        this.favoriteImageDao = db.favoriteImageDao();
    }

    // 更新数据并使用 DiffUtil
    public void updateImagePaths(List<String> newImagePaths) {

        imagePathsOld = imagePaths;
        imagePaths = newImagePaths;
        DiffUtil.Callback diffCallback = DiffUtilUtils.createImageDiffCallback(imagePathsOld, imagePaths);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imagePath = imagePaths.get(position);

        Glide.with(context)
                .load("file:///android_asset/" + imagePath)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageView);

        holder.imageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CameraActivity.class);
            intent.putExtra("imagePath", imagePath);
            context.startActivity(intent);
        });

        updateFavoriteButton(holder, imagePath);

        holder.favoriteButton.setOnClickListener(v -> {
            toggleFavorite(holder, imagePath);
        });
    }

    @Override
    public int getItemCount() {
        return imagePaths.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView favoriteButton;

        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.image_view);
            favoriteButton = view.findViewById(R.id.btn_favorite);
        }
    }

    private static class MyDiffCallback extends DiffUtil.Callback {
        private List<String> oldItems;
        private List<String> newItems;

        MyDiffCallback(List<String> oldItems, List<String> newItems) {
            this.oldItems = oldItems;
            this.newItems = newItems;
        }

        @Override
        public int getOldListSize() {
            return oldItems.size();
        }

        @Override
        public int getNewListSize() {
            return newItems.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return Objects.equals(oldItems.get(oldItemPosition), newItems.get(newItemPosition));
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            String newItem = newItems.get(newItemPosition);
            String oldItem = oldItems.get(oldItemPosition);
            return Objects.equals(oldItem, newItem);
        }
    }

    private void updateFavoriteButton(ViewHolder holder, String imagePath) {
        LiveData<FavoriteImage> favoriteImageLiveData = favoriteImageDao.getFavoriteImageByPath(imagePath);
        favoriteImageLiveData.observe((LifecycleOwner) context, new Observer<FavoriteImage>() {
            @Override
            public void onChanged(FavoriteImage favoriteImage) {
                boolean isFavorite = favoriteImage != null && favoriteImage.isFavorite();
                int iconResId = isFavorite ? R.drawable.favorite : R.drawable.unfavorite;
                holder.favoriteButton.setImageResource(iconResId);
            }
        });
    }

    private void toggleFavorite(ViewHolder holder, String imagePath) {

        AppDatabase.getDatabaseWriteExecutor().execute(() -> {

            FavoriteImage favoriteImage = favoriteImageDao.getByImagePath(imagePath);
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("-----------", "onChanged  favoriteImage=" + favoriteImage);
                    if (favoriteImage != null) {
                        removeImageFromFavorites(imagePath);
                        holder.favoriteButton.setImageResource(R.drawable.unfavorite);
                    } else {
                        addImageToFavorites(imagePath);
                        holder.favoriteButton.setImageResource(R.drawable.favorite);
                    }
                }
            });

        });



    }

    private void addImageToFavorites(String imagePath) {
        AppDatabase.getDatabaseWriteExecutor().execute(() -> {
            FavoriteImage favoriteImage = new FavoriteImage(true, imagePath);
            favoriteImageDao.insert(favoriteImage);
        });
    }

    private void removeImageFromFavorites(String imagePath) {
        AppDatabase.getDatabaseWriteExecutor().execute(() -> {
            favoriteImageDao.deleteByImagePath(imagePath);
        });
    }
}

