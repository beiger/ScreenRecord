package com.bing.example.main.home.floatingview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewManager;

public class FloatHelper {
	private static final int OVERLAY_PERMISSION_REQUEST_CODE = 100;

	private AppCompatActivity mActivity;

	public FloatHelper(AppCompatActivity activity) {
		mActivity = activity;
	}

	@TargetApi(Build.VERSION_CODES.M)
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
			showFloatingView(false, true);
		}
	}

	@SuppressLint("NewApi")
	public void showFloatingView(boolean isShowOverlayPermission, boolean isCustomFloatingView) {
		// API22以下かチェック
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
			startFloatingViewService(mActivity, isCustomFloatingView);
			return;
		}

		// 他のアプリの上に表示できるかチェック
		if (Settings.canDrawOverlays(mActivity)) {
			startFloatingViewService(mActivity, isCustomFloatingView);
			return;
		}

		// オーバレイパーミッションの表示
		if (isShowOverlayPermission) {
			final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + mActivity.getPackageName()));
			mActivity.startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE);
		}
	}

	public void startFloatingViewService(Activity activity, boolean isCustomFloatingView) {
		// *** You must follow these rules when obtain the cutout(FloatingViewManager.findCutoutSafeArea) ***
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
			// 1. 'windowLayoutInDisplayCutoutMode' do not be set to 'never'
			if (activity.getWindow().getAttributes().layoutInDisplayCutoutMode == WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER) {
				throw new RuntimeException("'windowLayoutInDisplayCutoutMode' do not be set to 'never'");
			}
			// 2. Do not set Activity to landscape
			if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				throw new RuntimeException("Do not set Activity to landscape");
			}
		}

		// launch service
		final Class<? extends Service> service;
		final String key;
		service = CustomFloatingViewService.class;
		key = CustomFloatingViewService.EXTRA_CUTOUT_SAFE_AREA;

		final Intent intent = new Intent(activity, service);
		intent.putExtra(key, FloatingViewManager.findCutoutSafeArea(activity));
		ContextCompat.startForegroundService(activity, intent);
	}

}
