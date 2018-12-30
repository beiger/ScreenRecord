package com.bing.example.search

import android.app.Application

import com.bing.example.model.RepositoryManager
import com.bing.example.model.entity.VideoInfo
import com.bing.mvvmbase.base.AppExecutors
import com.bing.mvvmbase.base.BaseViewModel
import com.bing.mvvmbase.model.datawrapper.Status
import com.blankj.utilcode.util.FileUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SearchViewModel(application: Application) : BaseViewModel(application) {
        val videoInfos: LiveData<List<VideoInfo>> = RepositoryManager.instance().loadVideosLiveData()
        val loadStatus: MutableLiveData<Status> = MutableLiveData()

        init {
                loadStatus.value = Status.SUCCESS
        }

        fun deleteVideos(infos: List<VideoInfo>) {
                AppExecutors.diskIO.execute {
                        RepositoryManager.instance().deleteVideo(infos)
                        for (i in infos.indices) {
                                FileUtils.deleteFile(infos[i].path)
                                FileUtils.deleteFile(infos[i].imagePath)
                        }
                }
        }

        fun deleteVideo(info: VideoInfo) {
                AppExecutors.diskIO.execute {
                        RepositoryManager.instance().deleteVideo(info)
                        FileUtils.deleteFile(info.path)
                        FileUtils.deleteFile(info.imagePath)
                }
        }
}
