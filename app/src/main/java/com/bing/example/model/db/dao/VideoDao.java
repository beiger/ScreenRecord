package com.bing.example.model.db.dao;


import com.bing.example.model.entity.VideoInfo;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface VideoDao {
        @Query("SELECT * FROM videos")
        List<VideoInfo> loadAllVideos();

        @Query("SELECT * FROM videos")
        LiveData<List<VideoInfo>> loadAllVideosLiveData();

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insertAll(List<VideoInfo> details);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insert(VideoInfo detail);

        @Query("delete from videos where 1=1")
        void deleteAll();

        @Query("delete from videos where id=(:id)")
        void deleteVideos(int id);

        @Delete
        void deleteVideos(List<VideoInfo> videoInfos);

        @Delete
        void deleteVideo(VideoInfo videoInfo);

        @Update
        void updateVideo(VideoInfo videoInfo);
}
