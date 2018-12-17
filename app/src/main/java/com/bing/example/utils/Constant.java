package com.bing.example.utils;

import android.os.Environment;

import com.bing.example.app.ScreenRecordApplication;

import java.io.File;

public class Constant {
	public static final String VIDEO_DIR = Environment.getExternalStorageDirectory() + File.separator + "ScreenRecord" + File.separator;
	public static final String FILE_DIR = ScreenRecordApplication.sContext.getFilesDir()+ File.separator + "info" + File.separator;
	public static final String THUMB_DIR = ScreenRecordApplication.sContext.getFilesDir() + File.separator + "thumb" + File.separator;
}
