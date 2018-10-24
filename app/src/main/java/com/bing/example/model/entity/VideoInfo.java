package com.bing.example.model.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "videos")
public class VideoInfo {

        @PrimaryKey(autoGenerate = true)
        private int id;
        private String title;
        private String path;
        private String imagePath;

        public VideoInfo() {
        }

        public VideoInfo(int id, String title, String path, String imagePath) {
                this.id = id;
                this.title = title;
                this.path = path;
                this.imagePath = imagePath;
        }

        public int getId() {
                return id;
        }

        public void setId(int id) {
                this.id = id;
        }

        public String getTitle() {
                return title;
        }

        public void setTitle(String title) {
                this.title = title;
        }

        public String getPath() {
                return path;
        }

        public void setPath(String path) {
                this.path = path;
        }

        public String getImagePath() {
                return imagePath;
        }

        public void setImagePath(String imagePath) {
                this.imagePath = imagePath;
        }
}
