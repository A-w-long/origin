package com.sketch.papertracingart.Room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ImageEntryDao {
    @Insert
    void insert(ImageEntry imageEntry);

    @Delete
    void delete(ImageEntry imageEntry);

    @Query("SELECT * FROM image_entry")
    List<ImageEntry> getAllImages();

    @Query("SELECT * FROM image_entry WHERE imagePath = :imagePath LIMIT 1")
    ImageEntry findByPath(String imagePath);
}
