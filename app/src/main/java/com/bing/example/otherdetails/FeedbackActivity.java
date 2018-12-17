package com.bing.example.otherdetails;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bing.example.R;
import com.bing.example.databinding.ActivityFeedbackBinding;
import com.bing.example.utils.BitmapUtil;
import com.bing.example.utils.Constant;
import com.bing.example.utils.DeviceInfo;
import com.bing.example.utils.SystemLog;
import com.bing.example.utils.UriUtil;
import com.bing.example.widget.PermissionDialog;
import com.bing.mvvmbase.base.BaseActivity;
import com.bing.mvvmbase.base.BaseViewModel;
import com.bing.mvvmbase.utils.UiUtil;
import com.blankj.utilcode.util.FileIOUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;

public class FeedbackActivity extends BaseActivity<ActivityFeedbackBinding, BaseViewModel> implements View.OnClickListener {
        public String LOG_TO_STRING = SystemLog.extractLogToString();
        private EditText mEditText;
        private final String EMAIL = "dashuiren000@163.com";
        private String mDeviceInfo;
        private final String DEVICE_INFO_FILENAME = Constant.FILE_DIR + "deviceInf.txt";
        private final String DEVICE_LOG_FILENAME = Constant.FILE_DIR + "deviceLog.txt";
        private final boolean WITHINFO = true;
        private int PICK_IMAGE_REQUEST = 125;
        private String mImagePath;
        private Uri mImageUri;
        private ImageView selectedImageView;
        private LinearLayout selectContainer;

        @Override
        public void onCreateFirst() {
                UiUtil.setBarColorAndFontBlack(this, Color.TRANSPARENT);
        }

        @Override
        public int layoutId() {
                return R.layout.activity_feedback;
        }

        @Override
        public void bindAndObserve() {
                init();
        }

        private void init() {
                mEditText = mBinding.editText;
                TextView info = mBinding.infoLegal;
                selectedImageView = mBinding.selectedImageView;
                selectContainer = mBinding.selectContainer;
                mBinding.submitSuggestion.setOnClickListener(this);
                mBinding.selectImage.setOnClickListener(this);
                mBinding.back.setOnClickListener(this);

                mDeviceInfo = DeviceInfo.getAllDeviceInfo(this, false);
                if (WITHINFO) {
                        CharSequence infoFeedbackStart = getResources().getString(R.string.info_fedback_legal_start);
                        SpannableString deviceInfo = new SpannableString(getResources().getString(R.string.info_fedback_legal_system_info));
                        deviceInfo.setSpan(new ClickableSpan() {
                                @Override
                                public void onClick(@NonNull View widget) {
                                        new AlertDialog.Builder(FeedbackActivity.this)
                                                .setTitle(R.string.info_fedback_legal_system_info)
                                                .setMessage(mDeviceInfo)
                                                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                                                .show();
                                }
                        }, 0, deviceInfo.length(), 0);
                        CharSequence infoFeedbackAnd = getResources().getString(R.string.info_fedback_legal_and);
                        SpannableString systemLog = new SpannableString(getResources().getString(R.string.info_fedback_legal_log_data));
                        systemLog.setSpan(new ClickableSpan() {
                                @Override
                                public void onClick(@NonNull View widget) {
                                        new AlertDialog.Builder(FeedbackActivity.this)
                                                .setTitle(R.string.info_fedback_legal_log_data)
                                                .setMessage(LOG_TO_STRING)
                                                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                                                .show();
                                }
                        }, 0, systemLog.length(), 0);
                        CharSequence infoFeedbackEnd = getResources().getString(R.string.info_fedback_legal_will_be_sent, getAppLabel());
                        Spanned finalLegal = (Spanned) TextUtils.concat(infoFeedbackStart, deviceInfo, infoFeedbackAnd, systemLog, infoFeedbackEnd);

                        info.setText(finalLegal);
                        info.setMovementMethod(LinkMovementMethod.getInstance());
                } else {
                        info.setVisibility(View.GONE);
                }
        }

        public void selectImage() {
		addDisposable(new RxPermissions(this)
				.request(Manifest.permission.READ_EXTERNAL_STORAGE)
				.subscribe(aBoolean -> {
                                        if (aBoolean) {
                                                selectPicture();
                                        } else {
                                                PermissionDialog.show(getSupportFragmentManager(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
                                        }
                                }));
        }

        private void selectPicture() {
                mImagePath = null;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), PICK_IMAGE_REQUEST);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                if (requestCode == PICK_IMAGE_REQUEST &&
                        resultCode == RESULT_OK && data != null && data.getData() != null) {
                        mImageUri = data.getData();
                        mImagePath = UriUtil.getPath(this, data.getData());
                        selectedImageView.setImageBitmap(BitmapUtil.decodeSampledBitmapFromFile(mImagePath,
                                selectedImageView.getWidth(), selectedImageView.getHeight()));
                        selectContainer.setVisibility(View.GONE);
                }
                super.onActivityResult(requestCode, resultCode, data);
        }

        public void sendEmail(String body) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{EMAIL});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_mail_subject, getAppLabel()));
                emailIntent.putExtra(Intent.EXTRA_TEXT, body);

                ArrayList<Uri> uris = new ArrayList<>();
                if (WITHINFO) {
                        FileIOUtils.writeFileFromString(DEVICE_INFO_FILENAME, mDeviceInfo);
                        FileIOUtils.writeFileFromString(DEVICE_LOG_FILENAME, LOG_TO_STRING);
                        Uri deviceInfoUri = FileProvider.getUriForFile(this, "com.bing.example.fileprovider", new File(Constant.FILE_DIR + "deviceInf.txt"));
                        uris.add(deviceInfoUri);
                        Uri logUri = FileProvider.getUriForFile(this, "com.bing.example.fileprovider", new File(Constant.FILE_DIR + "deviceLog.txt"));
                        uris.add(logUri);
                }

                if (mImagePath != null) {
                        uris.add(mImageUri);
                }
                emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                startActivity(UriUtil.createEmailOnlyChooserIntent(this, emailIntent, getString(R.string.send_feedback_two)));
        }

        @Override
        public void onClick(@NotNull View view) {
                switch (view.getId()) {
                        case R.id.submitSuggestion:
                                String suggestion = mEditText.getText().toString();
                                if (suggestion.trim().length() > 0) {
                                        sendEmail(suggestion);
                                } else {
                                        mEditText.setError(getString(R.string.please_write));
                                }
                                break;

                        case R.id.selectImage:
                                selectImage();
                                break;

                        case R.id.back:
                                onBackPressed();
                                break;
                }
        }

        public String getAppLabel() {
                return getResources().getString(R.string.app_name);
        }

        @Override
        public void initViewModel() {
                mViewModel = ViewModelProviders.of(this).get(BaseViewModel.class);
        }
}
