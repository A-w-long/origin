package com.sketch.papertracingart.Room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FavoriteImageDao {

    @Insert
    void insert(FavoriteImage favoriteImage);

    @Insert
    void insertAll(List<FavoriteImage> favoriteImages);

    @Query("SELECT * FROM favorite_images")
    List<FavoriteImage> getAll();

    @Query("SELECT image_path FROM favorite_images")
    List<String> getAllImagePaths();

    @Query("SELECT * FROM favorite_images WHERE image_path = :imageUrl")
    FavoriteImage getByImagePath(String imageUrl);

    @Query("SELECT * FROM favorite_images WHERE is_favorite = 1")
    List<FavoriteImage> getAllFavorites();

    @Query("SELECT * FROM favorite_images WHERE image_path = :imageUrl")
    LiveData<FavoriteImage> getFavoriteImageByPath(String imageUrl);

    @Query("SELECT * FROM favorite_images WHERE is_favorite = 1")
    LiveData<List<FavoriteImage>> getAllFavoriteImages();

    @Update
    void update(FavoriteImage favoriteImage);

    @Update
    void updateAll(List<FavoriteImage> favoriteImages);

    @Delete
    void delete(FavoriteImage favoriteImage);

    @Delete
    void deleteAll(List<FavoriteImage> favoriteImages);

    @Query("DELETE FROM favorite_images WHERE image_path = :imageUrl")
    void deleteByImagePath(String imageUrl);

    @Query("SELECT COUNT(*) FROM favorite_images WHERE is_favorite = 1")
    int getFavoriteCount();

    @Query("SELECT * FROM favorite_images LIMIT :limit OFFSET :offset")
    List<FavoriteImage> getPaginated(int limit, int offset);

    @Query("SELECT * FROM favorite_images ORDER BY image_path ASC")
    List<FavoriteImage> getAllSortedByImagePath();
}

