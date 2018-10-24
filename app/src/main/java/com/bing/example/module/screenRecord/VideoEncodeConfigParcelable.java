package com.bing.example.module.screenRecord;

import android.media.MediaCodecInfo;
import android.os.Parcel;
import android.os.Parcelable;

public class VideoEncodeConfigParcelable implements Parcelable {
        public  int width;
        public  int height;
        public  int bitrate;
        public  int framerate;
        private int iframeInterval;
        private String codecName;
        private String mimeType;
        private int profile;
        private int level;

        public VideoEncodeConfigParcelable() {
        }

        public VideoEncodeConfigParcelable(int width, int height, int bitrate, int framerate, int iframeInterval, String codecName, String mimeType, int profile, int level) {
                this.width = width;
                this.height = height;
                this.bitrate = bitrate;
                this.framerate = framerate;
                this.iframeInterval = iframeInterval;
                this.codecName = codecName;
                this.mimeType = mimeType;
                this.profile = profile;
                this.level = level;
        }

        public VideoEncodeConfig toConfig() {
                MediaCodecInfo.CodecProfileLevel codecProfileLevel = new MediaCodecInfo.CodecProfileLevel();
                codecProfileLevel.profile = profile;
                codecProfileLevel.level = level;
                return new VideoEncodeConfig(width, height, bitrate, framerate, iframeInterval, codecName, mimeType, codecProfileLevel);
        }

        protected VideoEncodeConfigParcelable(Parcel in) {
                width = in.readInt();
                height = in.readInt();
                bitrate = in.readInt();
                framerate = in.readInt();
                iframeInterval = in.readInt();
                codecName = in.readString();
                mimeType = in.readString();
                profile = in.readInt();
                level = in.readInt();
        }

        public static final Creator<VideoEncodeConfigParcelable> CREATOR = new Creator<VideoEncodeConfigParcelable>() {
                @Override
                public VideoEncodeConfigParcelable createFromParcel(Parcel in) {
                        return new VideoEncodeConfigParcelable(in);
                }

                @Override
                public VideoEncodeConfigParcelable[] newArray(int size) {
                        return new VideoEncodeConfigParcelable[size];
                }
        };

        public int getWidth() {
                return width;
        }

        public void setWidth(int width) {
                this.width = width;
        }

        public int getHeight() {
                return height;
        }

        public void setHeight(int height) {
                this.height = height;
        }

        public int getBitrate() {
                return bitrate;
        }

        public void setBitrate(int bitrate) {
                this.bitrate = bitrate;
        }

        public int getFramerate() {
                return framerate;
        }

        public void setFramerate(int framerate) {
                this.framerate = framerate;
        }

        public int getIframeInterval() {
                return iframeInterval;
        }

        public void setIframeInterval(int iframeInterval) {
                this.iframeInterval = iframeInterval;
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

        public int getProfile() {
                return profile;
        }

        public void setProfile(int profile) {
                this.profile = profile;
        }

        public int getLevel() {
                return level;
        }

        public void setLevel(int level) {
                this.level = level;
        }

        @Override
        public int describeContents() {
                return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(width);
                dest.writeInt(height);
                dest.writeInt(bitrate);
                dest.writeInt(framerate);
                dest.writeInt(iframeInterval);
                dest.writeString(codecName);
                dest.writeString(mimeType);
                dest.writeInt(profile);
                dest.writeInt(level);
        }
}
