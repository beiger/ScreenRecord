package com.bing.example.main.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Toast;

import com.bing.example.app.ScreenRecordApplication;
import com.bing.example.databinding.ActivityMainBinding;
import com.bing.example.main.fragment.VideoListFragment;
import com.bing.example.main.viewmodel.MainViewModel;
import com.bing.example.model.RepositoryManager;
import com.bing.example.model.entity.VideoInfo;
import com.bing.example.module.screenRecord.AudioEncodeConfig;
import com.bing.example.module.screenRecord.Notifications;
import com.bing.example.module.screenRecord.ScreenRecorder;
import com.bing.example.module.screenRecord.VideoEncodeConfig;
import com.bing.example.module.screenRecord.VideoEncodeConfigParcelable;
import com.bing.example.otherdetails.AboutActivity;
import com.bing.example.otherdetails.FeedbackActivity;
import com.bing.example.otherdetails.ProblemActivity;
import com.bing.example.utils.BitmapUtil;
import com.bing.example.utils.Constant;
import com.bing.mvvmbase.base.AppExecutors;
import com.bing.mvvmbase.base.BaseActivity;
import com.bing.mvvmbase.base.viewpager.BaseFragmentPagerAdapter;
import com.bing.mvvmbase.base.widget.CustomViewPager;
import com.bing.mvvmbase.utils.UiUtil;
import com.blankj.utilcode.util.ToastUtils;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

import com.bing.example.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import cn.jzvd.Jzvd;
import io.reactivex.Single;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> implements View.OnClickListener{
        public static final String ACTION_STOP = "net.yrom.screenrecorder.action.STOP";
        private static final int REQUEST_MEDIA_PROJECTION = 1;
        private static final int REQUEST_PERMISSIONS = 2;
        private static final int REQUEST_SETTINGS = 2;

        private VideoListFragment mVideoListFragment;
        private AppExecutors mAppExecutors;
        private MediaProjectionManager mMediaProjectionManager;
        private Notifications mNotifications;
        private ScreenRecorder mRecorder;
        private BroadcastReceiver mStopActionReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                        if (ACTION_STOP.equals(intent.getAction())) {
                                stopRecorder();
                        }
                }
        };

        private static File getSavingDir() {
                return new File(Constant.VIDEO_DIR);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

                Toolbar toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                ActionBar actionBar = getSupportActionBar();
	        assert actionBar != null;
	        actionBar.setTitle("");

                AccountHeader headerResult = new AccountHeaderBuilder()
                        .withActivity(this)
                        .withCompactStyle(false)
                        .withHeaderBackground(R.drawable.fall)
                        .withSavedInstance(savedInstanceState)
                        .build();

	        new DrawerBuilder()
			        .withActivity(this)
			        .withAccountHeader(headerResult)
			        .withToolbar(toolbar)
			        .withFullscreen(true)
			        .addDrawerItems(
					        new PrimaryDrawerItem().withName(R.string.config).withIcon(R.drawable.ic_setting).withIdentifier(1),
					        new PrimaryDrawerItem().withName(R.string.problems).withIcon(R.drawable.ic_question).withIdentifier(2),
					        new PrimaryDrawerItem().withName(R.string.feedback).withIcon(R.drawable.ic_feedback).withIdentifier(3),
					        new PrimaryDrawerItem().withName(R.string.about).withIcon(R.drawable.ic_about).withIdentifier(4)
			        )
			        .withSelectedItem(-1)
			        .withOnDrawerItemClickListener((view, position, drawerItem) -> {
				        if (drawerItem == null) {
					        return false;
				        }
				        if (drawerItem.getIdentifier() == 1) {
					        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
					        startActivityForResult(intent, REQUEST_SETTINGS);
				        } else  if (drawerItem.getIdentifier() == 2) {
                                                Intent intent = new Intent(MainActivity.this, ProblemActivity.class);
                                                startActivityForResult(intent, REQUEST_SETTINGS);
                                        } else  if (drawerItem.getIdentifier() == 3) {
                                                Intent intent = new Intent(MainActivity.this, FeedbackActivity.class);
                                                startActivityForResult(intent, REQUEST_SETTINGS);
                                        } else  if (drawerItem.getIdentifier() == 4) {
                                                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                                                startActivityForResult(intent, REQUEST_SETTINGS);
                                        }
				        return false;
			        })
			        .withSavedInstance(savedInstanceState)
			        .build();
        }

        @Override
        protected void onCreateFirst() {
                UiUtil.setBarColorAndFontBlack(this, Color.TRANSPARENT, 0);
        }

        @Override
        protected void initViewModel() {
                mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        }

        @Override
        protected int layoutId() {
                return R.layout.activity_main;
        }

        @Override
        protected void bindAndObserve() {
                mAppExecutors = ((ScreenRecordApplication) getApplication()).getAppExecutors();
                mMediaProjectionManager = (MediaProjectionManager) getApplicationContext().getSystemService(MEDIA_PROJECTION_SERVICE);
                mNotifications = new Notifications(getApplicationContext());

                mViewModel.getVideoEncodeConfigLive().observe(this, config -> {
                        if (config != null) {
                                mViewModel.setVideoEncodeConfig(config);
                        }
                });
                mViewModel.getAudioEncodeConfigLive().observe(this, audioEncodeConfig -> {
                        if (audioEncodeConfig != null) {
                                mViewModel.setAudioEncodeConfig(audioEncodeConfig);
                        }
                });
                mBinding.record.setOnClickListener(this);
                mBinding.back.setOnClickListener(this);
                mBinding.delete.setOnClickListener(this);
                mBinding.selectAll.setOnClickListener(this);
                initViewPager();
                mBinding.setIsNormalMode(mViewModel.getIsNormalMode());
        }

        void initViewPager() {
                CustomViewPager viewPager = mBinding.viewPager;
                viewPager.setScanScroll(false);
                mVideoListFragment = new VideoListFragment();
                List<Fragment> fragments = new ArrayList<>();
                fragments.add(mVideoListFragment);
                BaseFragmentPagerAdapter fragmentPagerAdapter = new BaseFragmentPagerAdapter(getSupportFragmentManager(), fragments);
                viewPager.setAdapter(fragmentPagerAdapter);
        }

        @Override
        public void onClick(View v) {
                switch (v.getId()) {
                        case R.id.record:
                                onButtonClick();
                                break;

                        case R.id.back:
                                mVideoListFragment.onClickBack();
                                break;

                        case R.id.delete:
                                mVideoListFragment.onClickDelete();
                                break;

                        case R.id.select_all:
                                mVideoListFragment.onClickSelectAll();
                                break;
                }
        }

        private void startCaptureIntent() {
                Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
                startActivityForResult(captureIntent, REQUEST_MEDIA_PROJECTION);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                if (requestCode == REQUEST_MEDIA_PROJECTION && resultCode == RESULT_OK) {
                        MediaProjection mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
                        if (mediaProjection == null) {
                                return;
                        }

                        VideoEncodeConfig video = mViewModel.getVideoEncodeConfig();
                        AudioEncodeConfig audio = mViewModel.getAudioEncodeConfig(); // audio can be null
                        if (video == null) {
                                ToastUtils.showShort("Create ScreenRecorder failure");
                                mediaProjection.stop();
                                return;
                        }

                        File dir = getSavingDir();
                        if (!dir.exists() && !dir.mkdirs()) {
                                cancelRecorder();
                                return;
                        }
                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US);
                        final File file = new File(dir, format.format(new Date()) + ".mp4");
                        mRecorder = newRecorder(mediaProjection, video, audio, file);
                        if (hasPermissions()) {
                                moveTaskToBack(false);
                                addDisposable(Single.timer(500, TimeUnit.MILLISECONDS).subscribe(aLong -> startRecorder()));
                        } else {
                                cancelRecorder();
                        }
                } else if (requestCode == REQUEST_SETTINGS) {
                        if (resultCode == RESULT_OK) {
	                        VideoEncodeConfigParcelable videoEncodeConfigParcelable = data.getParcelableExtra("video");
	                        if (videoEncodeConfigParcelable != null) {
	                        	mViewModel.setVideoEncodeConfig(videoEncodeConfigParcelable.toConfig());
	                        }
	                        AudioEncodeConfig audioEncodeConfig = data.getParcelableExtra("audio");
	                        if (audioEncodeConfig != null) {
	                        	mViewModel.setAudioEncodeConfig(audioEncodeConfig);
	                        }
                        }
                }
        }

        private ScreenRecorder newRecorder(MediaProjection mediaProjection, VideoEncodeConfig video,
                                           AudioEncodeConfig audio, final File output) {
                ScreenRecorder r = new ScreenRecorder(video, audio,
                        1, mediaProjection, output.getAbsolutePath());
                r.setCallback(new ScreenRecorder.Callback() {
                        long startTime = 0;

                        @Override
                        public void onStop(Throwable error) {
                                StrictMode.VmPolicy vmPolicy = StrictMode.getVmPolicy();
                                File file = new File(mRecorder.getSavedPath());
                                mRecorder = null;
                                try {
                                        // disable detecting FileUriExposure on public file
                                        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build());
                                        insertIntoDatabase(file);
                                        ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
                                        am.moveTaskToFront(getTaskId(), 0);
                                } finally {
                                        StrictMode.setVmPolicy(vmPolicy);
                                }
                                if (error != null) {
                                        ToastUtils.showShort("Recorder error ! See logcat for more details");
                                        error.printStackTrace();
                                        output.delete();
                                } else {
                                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                                                .addCategory(Intent.CATEGORY_DEFAULT)
                                                .setData(Uri.fromFile(output));
                                        sendBroadcast(intent);
                                }
                        }

                        private void insertIntoDatabase(final File file) {
                                mAppExecutors.diskIO().execute(() -> {
                                        String imagePath = Constant.THUMB_DIR + file.getName();
                                        Bitmap bitmap = BitmapUtil.createVideoThumbnailLocal(file.getAbsolutePath(), 0);
                                        BitmapUtil.saveBitmap(bitmap, imagePath);
                                        VideoInfo videoInfo = new VideoInfo(0, file.getName(), file.getAbsolutePath(), imagePath);
                                        RepositoryManager.instance().insertVideo(videoInfo);
                                });
                        }

                        @Override
                        public void onStart() {
                                mNotifications.recording(0);
                        }

                        @Override
                        public void onRecording(long presentationTimeUs) {
                                if (startTime <= 0) {
                                        startTime = presentationTimeUs;
                                }
                                long time = (presentationTimeUs - startTime) / 1000;
                                mNotifications.recording(time);
                        }
                });
                return r;
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                if (requestCode == REQUEST_PERMISSIONS) {
                        int granted = PackageManager.PERMISSION_GRANTED;
                        for (int r : grantResults) {
                                granted |= r;
                        }
                        if (granted == PackageManager.PERMISSION_GRANTED) {
                                startCaptureIntent();
                        } else {
                                ToastUtils.showShort(getString(R.string.no_permission));
                        }
                }
        }

	@Override
	public void onBackPressed() {
		if (Jzvd.backPress()) {
			return;
		}
		if (mVideoListFragment.onBackPressed()) {
		        return;
                }
		super.onBackPressed();
	}
	@Override
	protected void onPause() {
		super.onPause();
		Jzvd.releaseAllVideos();
	}

        @Override
        protected void onDestroy() {
                super.onDestroy();
                stopRecorder();
        }

        private void onButtonClick() {
                mVideoListFragment.intoNormalMode();
                if (mRecorder != null) {
                        stopRecorder();
                } else if (hasPermissions()) {
                        startCaptureIntent();
                } else if (Build.VERSION.SDK_INT >= M) {
                        requestPermissions();
                } else {
                        ToastUtils.showShort("No permission to write sd card");
                }
        }

        private void startRecorder() {
                if (mRecorder == null) return;
                mRecorder.start();
                registerReceiver(mStopActionReceiver, new IntentFilter(ACTION_STOP));
        }

        private void stopRecorder() {
                mNotifications.clear();
                if (mRecorder != null) {
                        mRecorder.quit();
                }
                try {
                        unregisterReceiver(mStopActionReceiver);
                } catch (Exception e) {
                        //ignored
                }
        }

        private void cancelRecorder() {
                if (mRecorder == null) return;
                Toast.makeText(this, "Permission denied! Screen recorder is cancel", Toast.LENGTH_SHORT).show();
                stopRecorder();
        }

        @TargetApi(M)
        private void requestPermissions() {
                final String[] permissions = mViewModel.getAudioEncodeConfig() != null
                        ? new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}
                        : new String[]{WRITE_EXTERNAL_STORAGE};
                boolean showRationale = false;
                for (String perm : permissions) {
                        showRationale |= shouldShowRequestPermissionRationale(perm);
                }
                if (!showRationale) {
                        requestPermissions(permissions, REQUEST_PERMISSIONS);
                        return;
                }
                new AlertDialog.Builder(this)
                        .setMessage(R.string.request_permission)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> requestPermissions(permissions, REQUEST_PERMISSIONS))
                        .setNegativeButton(android.R.string.cancel, null)
                        .create()
                        .show();
        }

        private boolean hasPermissions() {
                PackageManager pm = getPackageManager();
                String packageName = getPackageName();
                int granted = (mViewModel.getAudioEncodeConfig() != null ? pm.checkPermission(RECORD_AUDIO, packageName) : PackageManager.PERMISSION_GRANTED)
                        | pm.checkPermission(WRITE_EXTERNAL_STORAGE, packageName);
                return granted == PackageManager.PERMISSION_GRANTED;
        }
}
