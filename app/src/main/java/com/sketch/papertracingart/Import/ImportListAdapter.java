package com.sketch.papertracingart.Import;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

/**
 * RecyclerView 的适配器，用于显示图片。
 */
public class ImportListAdapter extends RecyclerView.Adapter<ImportListAdapter.ImageViewHolder> {

    private List<String> imagePaths;

    private List<String> imagePathsOld;
    private final FavoriteImageDao favoriteImageDao;
    private final Activity context;// 图片路径列表

    public ImportListAdapter(List<String> imagePaths, Activity context) {
        this.imagePaths = imagePaths;
        this.context = context;
        AppDatabase db = AppDatabase.getInstance(context);
        this.favoriteImageDao = db.favoriteImageDao(); // 初始化 DAO
    }

    // 更新数据并使用 DiffUtil
    public void updateImagePaths(List<String> newImagePaths) {

//        imagePathsOld = imagePaths;
        imagePaths = newImagePaths;
//        DiffUtil.Callback diffCallback = DiffUtilUtils.createImageDiffCallback(imagePathsOld, imagePaths);
//        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
//        diffResult.dispatchUpdatesTo(this);

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 创建并返回 ImageViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imagePath = imagePaths.get(position); // 获取当前图片的路径
        Log.d("imagepTh","imagePath"+imagePath);
        Log.d("imagepTh","imagePaths"+imagePath);
        Context context = holder.imageView.getContext();

        if (imagePath.startsWith("/data/user/")) {
            Glide.with(context)
                    .load(imagePath)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imageView);
        } else {
            Glide.with(context)
                    .load("file:///android_asset/" + imagePath)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imageView);
        }

        holder.imageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CameraActivity.class);
            intent.putExtra("imagePath", imagePath);
            context.startActivity(intent);
        });

        // 更新收藏按钮状态
        updateFavoriteButton(holder, imagePath);

        // 设置收藏按钮点击事件
        holder.favoriteButton.setOnClickListener(v -> {
            // 检查当前图片是否被收藏
            new CheckFavoriteTask(holder, imagePath).execute();
        });

    }

    @Override
    public int getItemCount() {
        return imagePaths.size(); // 返回图片的数量
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



    /**
     * ViewHolder 类，用于缓存视图
     */
    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView favoriteButton;

        ImageViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.image_view);
            favoriteButton = view.findViewById(R.id.btn_favorite);
        }
    }

    // 更新收藏按钮状态（通过异步任务）
    private void updateFavoriteButton(ImportListAdapter.ImageViewHolder holder, String imagePath) {
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

    private void toggleFavorite(ImportListAdapter.ImageViewHolder holder, String imagePath) {

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

    // 刷新所有图片的收藏状态
    public void refreshFavoriteStatusImport() {
        for (int i = 0; i < imagePaths.size(); i++) {
            notifyItemChanged(i);
        }
    }

    // 添加图片到收藏数据库
    private void addImageToFavorites(String imagePath) {
        AsyncTask.execute(() -> {
            FavoriteImage favoriteImage = favoriteImageDao.getByImagePath(imagePath);
            if (favoriteImage == null) {
                favoriteImage = new FavoriteImage(true, imagePath);
                favoriteImageDao.insert(favoriteImage); // 插入新的收藏记录
            } else {
                favoriteImage.setFavorite(true);
                favoriteImageDao.update(favoriteImage); // 更新收藏状态
            }
        });
    }

    // 从收藏数据库中移除图片
    private void removeImageFromFavorites(String imagePath) {
        AppDatabase.getDatabaseWriteExecutor().execute(() -> {
            favoriteImageDao.deleteByImagePath(imagePath);
        });
    }

    // 异步任务：检查图片是否收藏
    private class CheckFavoriteTask extends AsyncTask<Void, Void, Boolean> {
        private final ImportListAdapter.ImageViewHolder holder;
        private final String imagePath;

        CheckFavoriteTask(ImportListAdapter.ImageViewHolder holder, String imagePath) {
            this.holder = holder;
            this.imagePath = imagePath;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            FavoriteImage favoriteImage = favoriteImageDao.getByImagePath(imagePath);
            return favoriteImage != null && favoriteImage.isFavorite();
        }

        @Override
        protected void onPostExecute(Boolean isFavorite) {
            if (isFavorite) {
                // 如果已经收藏，则移除收藏
                removeImageFromFavorites(imagePath);
                holder.favoriteButton.setImageResource(R.drawable.unfavorite); // 更新按钮图标为未收藏状态
            } else {
                // 如果未收藏，则添加到收藏
                addImageToFavorites(imagePath);
                holder.favoriteButton.setImageResource(R.drawable.favorite); // 更新按钮图标为收藏状态
            }
        }
    }

}
