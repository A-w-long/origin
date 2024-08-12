package com.sketch.papertracingart.Room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "image_entry")
public class ImageEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String imagePath;

    public ImageEntry(String imagePath) {
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
}