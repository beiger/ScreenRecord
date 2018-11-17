package com.bing.example.model.entity;

import com.bing.mvvmbase.base.IsSame;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "videos")
public class VideoInfo implements IsSame {

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

        @Override
<<<<<<< HEAD
        public boolean equals(Object obj) {
                return id == ((VideoInfo)obj).id;
=======
        public boolean itemSame(IsSame isSame) {
                return id == ((VideoInfo)isSame).id;
        }

        @Override
        public boolean contentSame(IsSame isSame) {
                VideoInfo info = (VideoInfo)isSame;
                return id == info.id
                                && title.equals(info.title)
                                && path.equals(info.path)
                                && imagePath.equals(info.imagePath);
        }

        @Override
        public VideoInfo clone() {
                return new VideoInfo(id, title, path, imagePath);
>>>>>>> 1 fix bugs
        }
}
