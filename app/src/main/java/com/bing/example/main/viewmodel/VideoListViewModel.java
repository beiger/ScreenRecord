package com.bing.example.main.viewmodel;

import android.app.Application;

import com.bing.example.model.RepositoryManager;
import com.bing.example.model.entity.VideoInfo;
import com.bing.mvvmbase.base.BaseViewModel;
import com.bing.mvvmbase.model.datawrapper.Status;
import com.blankj.utilcode.util.FileUtils;

import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class VideoListViewModel extends BaseViewModel {
        private LiveData<List<VideoInfo>> mVideoInfos;
        private MutableLiveData<Status> mLoadStatus;

        public VideoListViewModel(@NonNull Application application) {
                super(application);
                mVideoInfos = RepositoryManager.instance().loadVideosLiveData();
                mLoadStatus = new MutableLiveData<>();
                mLoadStatus.setValue(Status.SUCCESS);
        }

        public LiveData<List<VideoInfo>> getVideoInfos() {
                return mVideoInfos;
        }

        public MutableLiveData<Status> getLoadStatus() {
                return mLoadStatus;
        }

        public void deleteVideos(List<VideoInfo> infos) {
                mAppExecutors.diskIO().execute(() -> {
                        RepositoryManager.instance().deleteVideo(infos);
                        for (int i = 0; i < infos.size(); i++) {
                                FileUtils.deleteFile(infos.get(i).getPath());
                                FileUtils.deleteFile(infos.get(i).getImagePath());
                        }
                });
        }

        public void deleteVideo(VideoInfo info) {
                mAppExecutors.diskIO().execute(() -> {
                        RepositoryManager.instance().deleteVideo(info);
                        FileUtils.deleteFile(info.getPath());
                        FileUtils.deleteFile(info.getImagePath());
                });
        }
}
