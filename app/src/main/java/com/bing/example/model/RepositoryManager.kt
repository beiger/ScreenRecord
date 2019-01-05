package com.bing.example.model

import com.bing.example.model.db.AppDatabase
import com.bing.example.model.entity.VideoInfo

import androidx.lifecycle.LiveData
import com.bing.mvvmbase.base.BaseApplication

class RepositoryManager private constructor() {
        private val mDatabase: AppDatabase = AppDatabase.getInstance(BaseApplication.sContext)

        fun loadVideosLiveData(): LiveData<List<VideoInfo>> {
                return mDatabase.videoDao().loadAllVideosLiveData()
        }

        fun loadVideos(): List<VideoInfo> {
                return mDatabase.videoDao().loadAllVideos()
        }

        fun insertVideo(videoInfo: VideoInfo) {
                mDatabase.videoDao().insert(videoInfo)
        }

        fun deleteVideo(videoInfos: List<VideoInfo>) {
                mDatabase.videoDao().deleteVideos(videoInfos)
        }

        fun deleteVideo(videoInfo: VideoInfo) {
                mDatabase.videoDao().deleteVideo(videoInfo)
        }

        fun updateVideo(videoInfo: VideoInfo) {
                mDatabase.videoDao().updateVideo(videoInfo)
        }

        companion object {
                private var sInstance: RepositoryManager? = null

                fun instance(): RepositoryManager {
                        if (sInstance == null) {
                                synchronized(RepositoryManager::class.java) {
                                        if (sInstance == null) {
                                                sInstance = RepositoryManager()
                                        }
                                }
                        }
                        return sInstance!!
                }
        }
}
