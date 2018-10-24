package com.bing.example.main.viewmodel;

import android.app.Application;

import com.alibaba.fastjson.JSON;
import com.bing.example.app.ScreenRecordApplication;
import com.bing.example.module.screenRecord.AudioEncodeConfig;
import com.bing.example.module.screenRecord.VideoEncodeConfig;
import com.bing.example.module.screenRecord.VideoEncodeConfigParcelable;
import com.bing.example.utils.Constant;
import com.bing.mvvmbase.base.AppExecutors;
import com.bing.mvvmbase.base.BaseViewModel;
import com.blankj.utilcode.util.FileIOUtils;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;

public class MainViewModel extends BaseViewModel {
        private AppExecutors mAppExecutors;
        private MutableLiveData<VideoEncodeConfig> mVideoEncodeConfigLive = new MutableLiveData<>();
        private VideoEncodeConfig mVideoEncodeConfig;
        private MutableLiveData<AudioEncodeConfig> mAudioEncodeConfigLive = new MutableLiveData<>();
        private AudioEncodeConfig mAudioEncodeConfig;
        private static final String VIDEO_CONFIG_PATH = Constant.FILE_DIR + "videoConfig.json";
        private static final String AUDIO_CONFIG_PATH = Constant.FILE_DIR + "audioConfig.json";

        private ObservableBoolean mIsNormalMode = new ObservableBoolean(true);

        public MainViewModel(@NonNull Application application) {
                super(application);
                mAppExecutors = ((ScreenRecordApplication) application).getAppExecutors();
                mAppExecutors.diskIO().execute(this::getConfigFromLocal);
        }

        private void getConfigFromLocal() {
                String vcString = FileIOUtils.readFile2String(VIDEO_CONFIG_PATH);
                if (vcString != null) {
                        VideoEncodeConfigParcelable videoEncodeConfigParcelable = JSON.parseObject(vcString, VideoEncodeConfigParcelable.class);
                        mVideoEncodeConfig = videoEncodeConfigParcelable.toConfig();
                } else {
                        VideoEncodeConfig.getDefault(mVideoEncodeConfigLive);
                }
                String acString = FileIOUtils.readFile2String(AUDIO_CONFIG_PATH);
                if (acString != null) {
                        mAudioEncodeConfig = JSON.parseObject(acString, AudioEncodeConfig.class);
                } else {
                        AudioEncodeConfig.getDefault(mAudioEncodeConfigLive);
                }
        }

        public void writeConfigToFile() {
                mAppExecutors.diskIO().execute(() -> {
                        if (mVideoEncodeConfig != null) {
                                String vcString = JSON.toJSONString(mVideoEncodeConfig.toParcelable());
                                FileIOUtils.writeFileFromString(VIDEO_CONFIG_PATH, vcString);
                        }
                        if (mAudioEncodeConfig != null) {
                                String acString = JSON.toJSONString(mAudioEncodeConfig);
                                FileIOUtils.writeFileFromString(AUDIO_CONFIG_PATH, acString);
                        }
                });
        }

        public VideoEncodeConfig getVideoEncodeConfig() {
                return mVideoEncodeConfig;
        }

        public AudioEncodeConfig getAudioEncodeConfig() {
                return mAudioEncodeConfig;
        }

        public void setVideoEncodeConfig(VideoEncodeConfig videoEncodeConfig) {
                mVideoEncodeConfig = videoEncodeConfig;
        }

        public void setAudioEncodeConfig(AudioEncodeConfig audioEncodeConfig) {
                mAudioEncodeConfig = audioEncodeConfig;
        }

        public MutableLiveData<VideoEncodeConfig> getVideoEncodeConfigLive() {
                return mVideoEncodeConfigLive;
        }

        public MutableLiveData<AudioEncodeConfig> getAudioEncodeConfigLive() {
                return mAudioEncodeConfigLive;
        }

        public ObservableBoolean getIsNormalMode() {
                return mIsNormalMode;
        }
}
