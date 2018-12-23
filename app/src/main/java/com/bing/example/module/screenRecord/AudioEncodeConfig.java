package com.bing.example.module.screenRecord

import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.os.Parcelable

import androidx.lifecycle.MutableLiveData

import com.bing.example.module.screenRecord.ScreenRecorder.AUDIO_AAC
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AudioEncodeConfig(var codecName: String,
                             var mimeType: String,
                             var bitRate: Int = 0,
                             var sampleRate: Int = 0,
                             var channelCount: Int = 0,
                             var profile: Int = 0) : Parcelable {

        fun toFormat(): MediaFormat {
                val format = MediaFormat.createAudioFormat(mimeType, sampleRate, channelCount)
                format.setInteger(MediaFormat.KEY_AAC_PROFILE, profile)
                format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate)
                //format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 4096 * 4);
                return format
        }

        override fun toString(): String {
                return "AudioEncodeConfig{" +
                        "codecName='" + codecName + '\''.toString() +
                        ", mimeType='" + mimeType + '\''.toString() +
                        ", bitRate=" + bitRate +
                        ", sampleRate=" + sampleRate +
                        ", channelCount=" + channelCount +
                        ", profile=" + profile +
                        '}'.toString()
        }

        companion object {
                fun getDefault(config: MutableLiveData<AudioEncodeConfig>) {
                        ScreenRecordUtil.findEncodersByTypeAsync(AUDIO_AAC
                        ) { infos ->
                                val profileLevel = ScreenRecordUtil.toProfileLevel(ScreenRecordUtil.aacProfiles()[0])
                                val profile = profileLevel?.profile
                                        ?: MediaCodecInfo.CodecProfileLevel.AACObjectMain
                                config.postValue(AudioEncodeConfig(infos[0].name, AUDIO_AAC, 240000, 44100, 2, profile))
                        }
                }
        }
}
