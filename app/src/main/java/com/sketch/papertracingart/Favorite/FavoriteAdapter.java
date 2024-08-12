package com.sketch.papertracingart.Favorite;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sketch.papertracingart.Camera.CameraActivity;
import com.sketch.papertracingart.R;
import com.sketch.papertracingart.Room.AppDatabase;
import com.sketch.papertracingart.Room.FavoriteImage;
import com.sketch.papertracingart.Room.FavoriteImageDao;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ImageViewHolder> {

    private List<FavoriteImage> favoriteImages;
    private final Context context;
    private final FavoriteImageDao favoriteImageDao;
    private final FavoriteFragment fragment;

    public FavoriteAdapter(Context context, FavoriteFragment fragment) {
        this.context = context;
        this.fragment = fragment;
        this.favoriteImages = new ArrayList<>();
        this.favoriteImageDao = AppDatabase.getInstance(context).favoriteImageDao();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        FavoriteImage favoriteImage = favoriteImages.get(position);
        String imagePath = favoriteImage.getImagePath();
        boolean isFavorite = favoriteImage.isFavorite();

        Glide.with(context)
                .load(imagePath.startsWith("/data/user/") ? imagePath : "file:///android_asset/" + imagePath)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageView);

        holder.favoriteButton.setImageResource(isFavorite ? R.drawable.favorite : R.drawable.unfavorite);

        holder.imageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CameraActivity.class);
            intent.putExtra("imagePath", imagePath);
            context.startActivity(intent);
        });

        holder.favoriteButton.setOnClickListener(v -> {
            new CheckFavoriteTask(holder, favoriteImage).execute();
        });
    }

    @Override
    public int getItemCount() {
        return favoriteImages.size();
    }

    public void updateData(List<FavoriteImage> newFavoriteImages) {
        favoriteImages = newFavoriteImages;
        notifyDataSetChanged();
    }

    private void updateFavoriteStatus(FavoriteImage favoriteImage, boolean isFavorite) {
        favoriteImage.setFavorite(isFavorite);
        AsyncTask.execute(() -> {
            if (isFavorite) {
                // 如果是收藏，插入新的记录
                if (favoriteImageDao.getByImagePath(favoriteImage.getImagePath()) == null) {
                    favoriteImageDao.insert(favoriteImage);
                } else {
                    // 如果记录已存在，则更新状态
                    favoriteImageDao.update(favoriteImage);
                }
            } else {
                // 如果是取消收藏，删除记录
                favoriteImageDao.delete(favoriteImage);
            }
            // 确保在 UI 线程中更新 RecyclerView
            fragment.requireActivity().runOnUiThread(() -> {
                // 刷新数据
                fragment.refreshData();
            });
        });
    }

    private class CheckFavoriteTask extends AsyncTask<Void, Void, Boolean> {
        private final ImageViewHolder holder;
        private final FavoriteImage favoriteImage;

        CheckFavoriteTask(ImageViewHolder holder, FavoriteImage favoriteImage) {
            this.holder = holder;
            this.favoriteImage = favoriteImage;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            FavoriteImage img = favoriteImageDao.getByImagePath(favoriteImage.getImagePath());
            return img != null && img.isFavorite();
        }

        @Override
        protected void onPostExecute(Boolean isFavorite) {
            if (isFavorite) {
                updateFavoriteStatus(favoriteImage, false);
                holder.favoriteButton.setImageResource(R.drawable.unfavorite);
//                Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
            } else {
                updateFavoriteStatus(favoriteImage, true);
                holder.favoriteButton.setImageResource(R.drawable.favorite);
                Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
            }
        }
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView favoriteButton;

        ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            favoriteButton = itemView.findViewById(R.id.btn_favorite);
        }
    }



}
