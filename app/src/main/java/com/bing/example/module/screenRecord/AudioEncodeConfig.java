/*
 * Copyright (c) 2017 Yrom Wang <http://www.yrom.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bing.example.module.screenRecord;

import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.Objects;

import androidx.lifecycle.MutableLiveData;

import static com.bing.example.module.screenRecord.ScreenRecorder.AUDIO_AAC;

/**
 * @author yrom
 * @version 2017/12/3
 */
public class AudioEncodeConfig implements Parcelable {
        public String codecName;
        private String mimeType;
        private int bitRate;
        public int sampleRate;
        public int channelCount;
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

        protected AudioEncodeConfig(Parcel in) {
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

        public String getMimeType() {
                return mimeType;
        }

        public int getBitRate() {
                return bitRate;
        }

        public int getSampleRate() {
                return sampleRate;
        }

        public int getChannelCount() {
                return channelCount;
        }

        public int getProfile() {
                return profile;
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
