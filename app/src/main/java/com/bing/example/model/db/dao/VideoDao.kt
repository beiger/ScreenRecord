package com.bing.example.model.db.dao


import com.bing.example.model.entity.VideoInfo

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface VideoDao {
        @Query("SELECT * FROM videos")
        fun loadAllVideos(): List<VideoInfo>

        @Query("SELECT * FROM videos")
        fun loadAllVideosLiveData(): LiveData<List<VideoInfo>>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insertAll(details: List<VideoInfo>)

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(detail: VideoInfo)

        @Query("delete from videos where 1=1")
        fun deleteAll()

        @Query("delete from videos where id=(:id)")
        fun deleteVideos(id: Int)

        @Delete
        fun deleteVideos(videoInfos: List<VideoInfo>)

        @Delete
        fun deleteVideo(videoInfo: VideoInfo)

        @Update
        fun updateVideo(videoInfo: VideoInfo)
}
