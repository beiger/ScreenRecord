package com.bing.example.otherdetails;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaCodecInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Range;
import android.widget.ImageView;

import com.bing.example.R;
import com.bing.example.module.screenRecord.AudioEncodeConfig;
import com.bing.example.module.screenRecord.ScreenRecordUtil;
import com.bing.example.module.screenRecord.VideoEncodeConfig;
import com.bing.example.module.screenRecord.VideoEncodeConfigParcelable;
import com.bing.example.widget.SettingSelectView;
import com.bing.mvvmbase.utils.UiUtil;
import com.blankj.utilcode.util.ScreenUtils;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import static com.bing.example.module.screenRecord.ScreenRecorder.AUDIO_AAC;
import static com.bing.example.module.screenRecord.ScreenRecorder.VIDEO_AVC;

public class SettingActivity extends AppCompatActivity {

        private SwitchButton mAudioToggle;
        private SettingSelectView mVieoResolution;
        private SettingSelectView mVideoFramerate;
        private SettingSelectView mIFrameInterval;
        private SettingSelectView mVideoBitrate;
        private SettingSelectView mAudioBitrate;
        private SettingSelectView mAudioSampleRate;
        private SettingSelectView mAudioChannelCount;
        private SettingSelectView mVideoCodec;
        private SettingSelectView mAudioCodec;
        private SettingSelectView mVideoProfileLevel;
        private SettingSelectView mAudioProfile;
        private SettingSelectView mOrientation;
        private MediaCodecInfo[] mAvcCodecInfos; // avc codecs
        private MediaCodecInfo[] mAacCodecInfos; // aac codecs

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                UiUtil.setBarColorAndFontBlack(this, Color.TRANSPARENT);
                setContentView(R.layout.activity_setting);
                bindViews();

                int width = ScreenUtils.getScreenWidth();
                int height = ScreenUtils.getScreenHeight();
                if (height > width) {
                        mVieoResolution.addContent(height + "x" + width);
                } else {
                        mVieoResolution.addContent(width + "x" + height);
                }
                ScreenRecordUtil.findEncodersByTypeAsync(VIDEO_AVC, infos -> {
                        mAvcCodecInfos = infos;
                        mVideoCodec.setContents(Arrays.asList(codecInfoNames(infos)));
                        restoreSelections(mVideoCodec, mVieoResolution, mVideoFramerate, mIFrameInterval, mVideoBitrate);
                });
                ScreenRecordUtil.findEncodersByTypeAsync(AUDIO_AAC,
                        infos -> {
                                mAacCodecInfos = infos;
                                mAudioCodec.setContents(Arrays.asList(codecInfoNames(infos)));
                                restoreSelections(mAudioCodec, mAudioChannelCount);
                        });
                mAudioToggle.setChecked(
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                .getBoolean(getResources().getResourceEntryName(mAudioToggle.getId()), true));
        }

        private static String[] codecInfoNames(MediaCodecInfo[] codecInfos) {
                String[] names = new String[codecInfos.length];
                for (int i = 0; i < codecInfos.length; i++) {
                        names[i] = codecInfos[i].getName();
                }
                return names;
        }

        private AudioEncodeConfig createAudioConfig() {
                if (!mAudioToggle.isChecked()) return null;
                String codec = getSelectedAudioCodec();
                if (codec == null) {
                        return null;
                }
                int bitrate = getSelectedAudioBitrate();
                int samplerate = getSelectedAudioSampleRate();
                int channelCount = getSelectedAudioChannelCount();
                int profile = getSelectedAudioProfile();

                return new AudioEncodeConfig(codec, AUDIO_AAC, bitrate, samplerate, channelCount, profile);
        }

        @Override
        public void onBackPressed() {
                VideoEncodeConfig config = createVideoConfig();
                VideoEncodeConfigParcelable configParcelable = null;
                if (config != null) {
                	configParcelable =  config.toParcelable();
                }
                AudioEncodeConfig audioEncodeConfig = createAudioConfig();
                Intent result = new Intent();
	        result.putExtra("video", configParcelable);
	        result.putExtra("audio", audioEncodeConfig);
                setResult(RESULT_OK, result);
                super.onBackPressed();
        }

	private VideoEncodeConfig createVideoConfig() {
		final String codec = getSelectedVideoCodec();
		if (codec == null) {
			// no selected codec ??
			return null;
		}
		// video size
		int[] selectedWithHeight = getSelectedWithHeight();
		boolean isLandscape = isLandscape();
		int width = selectedWithHeight[isLandscape ? 0 : 1];
		int height = selectedWithHeight[isLandscape ? 1 : 0];
		int framerate = getSelectedFramerate();
		int iframe = getSelectedIFrameInterval();
		int bitrate = getSelectedVideoBitrate();
		MediaCodecInfo.CodecProfileLevel profileLevel = getSelectedProfileLevel();
		return new VideoEncodeConfig(width, height, bitrate,
				framerate, iframe, codec, VIDEO_AVC, profileLevel);
	}

        @Override
        protected void onDestroy() {
                super.onDestroy();
                saveSelections();
        }


        private void bindViews() {
                mVideoCodec = findViewById(R.id.video_codec);
                mVieoResolution = findViewById(R.id.resolution);
                mVideoFramerate = findViewById(R.id.framerate);
                mIFrameInterval = findViewById(R.id.iframe_interval);
                mVideoBitrate = findViewById(R.id.video_bitrate);
                mOrientation = findViewById(R.id.orientation);

                mAudioCodec = findViewById(R.id.audio_codec);
                mVideoProfileLevel = findViewById(R.id.avc_profile);
                mAudioBitrate = findViewById(R.id.audio_bitrate);
                mAudioSampleRate = findViewById(R.id.sample_rate);
                mAudioProfile = findViewById(R.id.aac_profile);
                mAudioChannelCount = findViewById(R.id.audio_channel_count);

                mAudioToggle = findViewById(R.id.with_audio);

                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        mOrientation.setSelectPosition(1);
                }

                mVideoCodec.setListener((position, param) -> onVideoCodecSelected(param));
                mAudioCodec.setListener((position, param) -> onAudioCodecSelected(param));
                mVieoResolution.setListener(this::onResolutionChanged);
                mVideoFramerate.setListener(this::onFramerateChanged);
                mVideoBitrate.setListener(this::onBitrateChanged);
                mOrientation.setListener((selectedPosition, orientation) -> onOrientationChanged(selectedPosition));

	        ImageView back = findViewById(R.id.back);
	        back.setOnClickListener(v -> onBackPressed());
        }

        private void onResolutionChanged(int selectedPosition, String resolution) {
                String codecName = getSelectedVideoCodec();
                MediaCodecInfo codec = getVideoCodecInfo(codecName);
                if (codec == null) return;
                MediaCodecInfo.CodecCapabilities capabilities = codec.getCapabilitiesForType(VIDEO_AVC);
                MediaCodecInfo.VideoCapabilities videoCapabilities = capabilities.getVideoCapabilities();
                String[] xes = resolution.split("x");
                if (xes.length != 2) throw new IllegalArgumentException();
                boolean isLandscape = isLandscape();
                int width = Integer.parseInt(xes[isLandscape ? 0 : 1]);
                int height = Integer.parseInt(xes[isLandscape ? 1 : 0]);

                double selectedFramerate = getSelectedFramerate();
                int resetPos = Math.max(selectedPosition - 1, 0);
                if (!videoCapabilities.isSizeSupported(width, height)) {
                        mVieoResolution.setSelectPosition(resetPos);
                } else if (!videoCapabilities.areSizeAndRateSupported(width, height, selectedFramerate)) {
                        mVieoResolution.setSelectPosition(resetPos);
                }
        }

        private void onBitrateChanged(int selectedPosition, String bitrate) {
                String codecName = getSelectedVideoCodec();
                MediaCodecInfo codec = getVideoCodecInfo(codecName);
                if (codec == null) return;
                MediaCodecInfo.CodecCapabilities capabilities = codec.getCapabilitiesForType(VIDEO_AVC);
                MediaCodecInfo.VideoCapabilities videoCapabilities = capabilities.getVideoCapabilities();
                int selectedBitrate = Integer.parseInt(bitrate) * 1000;

                int resetPos = Math.max(selectedPosition - 1, 0);
                if (!videoCapabilities.getBitrateRange().contains(selectedBitrate)) {
                        mVideoBitrate.setSelectPosition(resetPos);
                }
        }

        private void onOrientationChanged(int selectedPosition) {
                String codecName = getSelectedVideoCodec();
                MediaCodecInfo codec = getVideoCodecInfo(codecName);
                if (codec == null) return;
                MediaCodecInfo.CodecCapabilities capabilities = codec.getCapabilitiesForType(VIDEO_AVC);
                MediaCodecInfo.VideoCapabilities videoCapabilities = capabilities.getVideoCapabilities();
                int[] selectedWithHeight = getSelectedWithHeight();
                boolean isLandscape = selectedPosition == 1;
                int width = selectedWithHeight[isLandscape ? 0 : 1];
                int height = selectedWithHeight[isLandscape ? 1 : 0];
                int resetPos = Math.max(mVieoResolution.getSelectPosition() - 1, 0);
                if (!videoCapabilities.isSizeSupported(width, height)) {
                        mVieoResolution.setSelectPosition(resetPos);
                }
        }

        private void onFramerateChanged(int selectedPosition, String rate) {
                String codecName = getSelectedVideoCodec();
                MediaCodecInfo codec = getVideoCodecInfo(codecName);
                if (codec == null) return;
                MediaCodecInfo.CodecCapabilities capabilities = codec.getCapabilitiesForType(VIDEO_AVC);
                MediaCodecInfo.VideoCapabilities videoCapabilities = capabilities.getVideoCapabilities();
                int[] selectedWithHeight = getSelectedWithHeight();
                int selectedFramerate = Integer.parseInt(rate);
                boolean isLandscape = isLandscape();
                int width = selectedWithHeight[isLandscape ? 0 : 1];
                int height = selectedWithHeight[isLandscape ? 1 : 0];

                int resetPos = Math.max(selectedPosition - 1, 0);
                if (!videoCapabilities.getSupportedFrameRates().contains(selectedFramerate)) {
                        mVideoFramerate.setSelectPosition(resetPos);
                } else if (!videoCapabilities.areSizeAndRateSupported(width, height, selectedFramerate)) {
                        mVideoFramerate.setSelectPosition(resetPos);
                }
        }

        private void onVideoCodecSelected(String codecName) {
                MediaCodecInfo codec = getVideoCodecInfo(codecName);
                if (codec == null) {
                        mVideoProfileLevel.setContents(null);
                        return;
                }
                MediaCodecInfo.CodecCapabilities capabilities = codec.getCapabilitiesForType(VIDEO_AVC);

                resetAvcProfileLevelAdapter(capabilities);
        }

        private void resetAvcProfileLevelAdapter(MediaCodecInfo.CodecCapabilities capabilities) {
                MediaCodecInfo.CodecProfileLevel[] profiles = capabilities.profileLevels;
                if (profiles == null || profiles.length == 0) {
                        mVideoProfileLevel.setEnabled(false);
                        return;
                }
                mVideoProfileLevel.setEnabled(true);
                String[] profileLevels = new String[profiles.length + 1];
                profileLevels[0] = "Default";
                for (int i = 0; i < profiles.length; i++) {
                        profileLevels[i + 1] = ScreenRecordUtil.avcProfileLevelToString(profiles[i]);
                }

                mVideoProfileLevel.setContents(Arrays.asList(profileLevels));
        }

        private void onAudioCodecSelected(String codecName) {
                MediaCodecInfo codec = getAudioCodecInfo(codecName);
                if (codec == null) {
                        mAudioProfile.setContents(null);
                        mAudioSampleRate.setContents(null);
                        mAudioBitrate.setContents(null);
                        return;
                }
                MediaCodecInfo.CodecCapabilities capabilities = codec.getCapabilitiesForType(AUDIO_AAC);

                resetAudioBitrateAdapter(capabilities);
                resetSampleRateAdapter(capabilities);
                resetAacProfileAdapter();
                restoreSelections(mAudioBitrate, mAudioSampleRate, mAudioProfile);
        }

        private void resetAacProfileAdapter() {
                String[] profiles = ScreenRecordUtil.aacProfiles();
                mAudioProfile.setContents(Arrays.asList(profiles));

        }

        private void resetSampleRateAdapter(MediaCodecInfo.CodecCapabilities capabilities) {
                int[] sampleRates = capabilities.getAudioCapabilities().getSupportedSampleRates();
                List<String> rates = new ArrayList<>(sampleRates.length);
                int preferred = -1;
                for (int i = 0; i < sampleRates.length; i++) {
                        int sampleRate = sampleRates[i];
                        if (sampleRate == 44100) {
                                preferred = i;
                        }
                        rates.add(sampleRate + "");
                }

                mAudioSampleRate.setContents(rates);
                mAudioSampleRate.setSelectPosition(preferred);
        }

        private void resetAudioBitrateAdapter(MediaCodecInfo.CodecCapabilities capabilities) {
                Range<Integer> bitrateRange = capabilities.getAudioCapabilities().getBitrateRange();
                int lower = Math.max(bitrateRange.getLower() / 1000, 80);
                int upper = bitrateRange.getUpper() / 1000;
                List<String> rates = new ArrayList<>();
                for (int rate = lower; rate < upper; rate += lower) {
                        rates.add(rate + "");
                }
                rates.add(upper + "");

                mAudioBitrate.setContents(rates);
                mAudioSampleRate.setSelectPosition(rates.size() / 2);
        }

        private MediaCodecInfo getVideoCodecInfo(String codecName) {
                if (codecName == null) return null;
                if (mAvcCodecInfos == null) {
                        mAvcCodecInfos = ScreenRecordUtil.findEncodersByType(VIDEO_AVC);
                }
                MediaCodecInfo codec = null;
	        for (MediaCodecInfo info : mAvcCodecInfos) {
		        if (info.getName().equals(codecName)) {
			        codec = info;
			        break;
		        }
	        }
                if (codec == null) return null;
                return codec;
        }

        private MediaCodecInfo getAudioCodecInfo(String codecName) {
                if (codecName == null) return null;
                if (mAacCodecInfos == null) {
                        mAacCodecInfos = ScreenRecordUtil.findEncodersByType(AUDIO_AAC);
                }
                MediaCodecInfo codec = null;
	        for (MediaCodecInfo info : mAacCodecInfos) {
		        if (info.getName().equals(codecName)) {
			        codec = info;
			        break;
		        }
	        }
                if (codec == null) return null;
                return codec;
        }

        private String getSelectedVideoCodec() {
                return mVideoCodec == null ? null : mVideoCodec.getSelectedItem();
        }

        private boolean isLandscape() {
                return mOrientation != null && mOrientation.getSelectPosition() == 1;
        }

        private int getSelectedFramerate() {
                if (mVideoFramerate == null) throw new IllegalStateException();
                return Integer.parseInt(mVideoFramerate.getSelectedItem());
        }

        private int getSelectedVideoBitrate() {
                if (mVideoBitrate == null) throw new IllegalStateException();
                String selectedItem = mVideoBitrate.getSelectedItem(); //kbps
                return Integer.parseInt(selectedItem) * 1000;
        }

        private int getSelectedIFrameInterval() {
                return (mIFrameInterval != null) ? Integer.parseInt(mIFrameInterval.getSelectedItem()) : 5;
        }

        private MediaCodecInfo.CodecProfileLevel getSelectedProfileLevel() {
                return mVideoProfileLevel != null ? ScreenRecordUtil.toProfileLevel(mVideoProfileLevel.getSelectedItem()) : null;
        }

        private int[] getSelectedWithHeight() {
                if (mVieoResolution == null) throw new IllegalStateException();
                String selected = mVieoResolution.getSelectedItem();
                String[] xes = selected.split("x");
                if (xes.length != 2) throw new IllegalArgumentException();
                return new int[]{Integer.parseInt(xes[0]), Integer.parseInt(xes[1])};

        }

        private String getSelectedAudioCodec() {
                return mAudioCodec == null ? null : mAudioCodec.getSelectedItem();
        }

        private int getSelectedAudioBitrate() {
                if (mAudioBitrate == null) throw new IllegalStateException();
                String selectedItem = mAudioBitrate.getSelectedItem();
                return Integer.parseInt(selectedItem) * 1000; // bps
        }

        private int getSelectedAudioSampleRate() {
                if (mAudioSampleRate == null) throw new IllegalStateException();
                return Integer.parseInt(mAudioSampleRate.getSelectedItem());
        }

        private int getSelectedAudioProfile() {
                if (mAudioProfile == null) throw new IllegalStateException();
                String selectedItem = mAudioProfile.getSelectedItem();
                MediaCodecInfo.CodecProfileLevel profileLevel = ScreenRecordUtil.toProfileLevel(selectedItem);
                return profileLevel == null ? MediaCodecInfo.CodecProfileLevel.AACObjectMain : profileLevel.profile;
        }

        private int getSelectedAudioChannelCount() {
                if (mAudioChannelCount == null) throw new IllegalStateException();
                String selectedItem = mAudioChannelCount.getSelectedItem();
                return Integer.parseInt(selectedItem);
        }

        private void restoreSelections(SettingSelectView... views) {
                SharedPreferences preferences =
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                for (SettingSelectView view : views) {
                        restoreSelectionFromPreferences(preferences, view);
                }
        }

        private void saveSelections() {
                SharedPreferences preferences =
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor edit = preferences.edit();
                for (SettingSelectView view : new SettingSelectView[]{
                        mVideoCodec,
                        mVieoResolution,
                        mVideoBitrate,
                        mVideoFramerate,
                        mIFrameInterval,
                        mAudioCodec,
                        mAudioChannelCount,
                        mAudioSampleRate,
                        mAudioBitrate,
                        mAudioProfile,
                }) {
                        saveSelectionToPreferences(edit, view);
                }
                edit.putBoolean(getResources().getResourceEntryName(mAudioToggle.getId()), mAudioToggle.isChecked());
                edit.apply();
        }

        private void saveSelectionToPreferences(SharedPreferences.Editor preferences, SettingSelectView view) {
                int resId = view.getId();
                String key = getResources().getResourceEntryName(resId);
                int selectedItemPosition = view.getSelectPosition();
                if (selectedItemPosition >= 0) {
                        preferences.putInt(key, selectedItemPosition);
                }
        }

        private void restoreSelectionFromPreferences(SharedPreferences preferences, SettingSelectView view) {
                int resId = view.getId();
                String key = getResources().getResourceEntryName(resId);
                int value = preferences.getInt(key, -1);
                if (value >= 0 && view.getContents() != null) {
                        view.setSelectPosition(value);
                }
        }

}
