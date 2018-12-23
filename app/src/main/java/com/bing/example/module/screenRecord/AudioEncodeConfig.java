package com.bing.example.module.screenRecord;

import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

import androidx.lifecycle.MutableLiveData;

import static com.bing.example.module.screenRecord.ScreenRecorder.AUDIO_AAC;

public class AudioEncodeConfig implements Parcelable {
        String codecName;
        private String mimeType;
        private int bitRate;
        int sampleRate;
        int channelCount;
        private int profile;

        public AudioEncodeConfig() {
        }

        public AudioEncodeConfig(String codecName, String mimeType,
                                 int bitRate, int sampleRate, int channelCount, int profile) {
                this.codecName = codecName;
                this.mimeType = Objects.requireNonNull(mimeType);
                this.bitRate = bitRate;
                this.sampleRate = sampleRate;
                this.channelCount = channelCount;
                this.profile = profile;
        }

        private AudioEncodeConfig(Parcel in) {
                codecName = in.readString();
                mimeType = in.readString();
                bitRate = in.readInt();
                sampleRate = in.readInt();
                channelCount = in.readInt();
                profile = in.readInt();
        }

        public static final Creator<AudioEncodeConfig> CREATOR = new Creator<AudioEncodeConfig>() {
                @Override
                public AudioEncodeConfig createFromParcel(Parcel in) {
                        return new AudioEncodeConfig(in);
                }

                @Override
                public AudioEncodeConfig[] newArray(int size) {
                        return new AudioEncodeConfig[size];
                }
        };

        public static void getDefault(MutableLiveData<AudioEncodeConfig> config) {
                ScreenRecordUtil.findEncodersByTypeAsync(AUDIO_AAC,
                        infos -> {
                                MediaCodecInfo.CodecProfileLevel profileLevel = ScreenRecordUtil.toProfileLevel(ScreenRecordUtil.aacProfiles()[0]);
                                int profile = profileLevel == null ? MediaCodecInfo.CodecProfileLevel.AACObjectMain : profileLevel.profile;
                                config.postValue(new AudioEncodeConfig(infos[0].getName(), AUDIO_AAC, 240000, 44100, 2, profile));
                        });
        }

        public String getCodecName() {
                return codecName;
        }

        public void setCodecName(String codecName) {
                this.codecName = codecName;
        }

        public String getMimeType() {
                return mimeType;
        }

        public void setMimeType(String mimeType) {
                this.mimeType = mimeType;
        }

        public int getBitRate() {
                return bitRate;
        }

        public void setBitRate(int bitRate) {
                this.bitRate = bitRate;
        }

        public int getSampleRate() {
                return sampleRate;
        }

        public void setSampleRate(int sampleRate) {
                this.sampleRate = sampleRate;
        }

        public int getChannelCount() {
                return channelCount;
        }

        public void setChannelCount(int channelCount) {
                this.channelCount = channelCount;
        }

        public int getProfile() {
                return profile;
        }

        public void setProfile(int profile) {
                this.profile = profile;
        }

        MediaFormat toFormat() {
                MediaFormat format = MediaFormat.createAudioFormat(mimeType, sampleRate, channelCount);
                format.setInteger(MediaFormat.KEY_AAC_PROFILE, profile);
                format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
                //format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 4096 * 4);
                return format;
        }

        @Override
        public String toString() {
                return "AudioEncodeConfig{" +
                        "codecName='" + codecName + '\'' +
                        ", mimeType='" + mimeType + '\'' +
                        ", bitRate=" + bitRate +
                        ", sampleRate=" + sampleRate +
                        ", channelCount=" + channelCount +
                        ", profile=" + profile +
                        '}';
        }

        @Override
        public int describeContents() {
                return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(codecName);
                dest.writeString(mimeType);
                dest.writeInt(bitRate);
                dest.writeInt(sampleRate);
                dest.writeInt(channelCount);
                dest.writeInt(profile);
        }
}
