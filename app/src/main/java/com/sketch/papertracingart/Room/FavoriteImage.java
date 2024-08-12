package com.sketch.papertracingart.Room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "favorite_images")
public class FavoriteImage implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "image_path")
    private String imagePath;

    @ColumnInfo(name = "is_favorite")
    private boolean isFavorite;  // 新字段：收藏状态

    public FavoriteImage(boolean isFavorite, String imagePath) {
        this.isFavorite = isFavorite;
        this.imagePath = imagePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
