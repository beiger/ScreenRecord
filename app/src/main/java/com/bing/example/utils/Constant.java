package com.bing.example.utils;

import android.os.Environment;

import com.bing.example.app.ScreenRecordApplication;

import java.io.File;

public class Constant {
	public static final String VIDEO_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + File.separator + "ScreenCaptures";
	public static final String FILE_DIR = ScreenRecordApplication.getContext().getFilesDir()+ File.separator;
	public static final String THUMB_DIR = ScreenRecordApplication.getContext().getFilesDir() + File.separator + "thumb" + File.separator;
}
