package com.bing.example.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bing.example.R;
import com.blankj.utilcode.util.ToastUtils;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class SettingSelectView extends LinearLayout {

        private String mTitle;
        private Drawable mIcon;
        private List<String> mContents;
        private int mSelectPosition;
        private OnSelectChangeListener mListener;
        private TextView mResult;
        private int mDefault;

        public SettingSelectView(Context context) {
                super(context);
                init(context, null, 0);
        }

        public SettingSelectView(Context context, @Nullable AttributeSet attrs) {
                super(context, attrs);
                init(context, attrs, 0);
        }

        public SettingSelectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
                super(context, attrs, defStyleAttr);
                init(context, attrs, defStyleAttr);
        }

        public String getTitle() {
                return mTitle;
        }

        public void setTitle(String title) {
                mTitle = title;
        }

        public List<String> getContents() {
                return mContents;
        }

        public void setContents(List<String> contents) {
                mContents = contents;
                if (mSelectPosition >= 0 && mContents != null && mSelectPosition < mContents.size()) {
                        mResult.setText(mContents.get(mSelectPosition));
                        if (mListener != null) {
                                mListener.onChange(mSelectPosition, mContents.get(mSelectPosition));
                        }
                }
        }

        public void addContent(String option) {
                if (mContents == null) {
                        mContents = new ArrayList<>();
                }
                if (!mContents.contains(option)) {
                        mContents.add(0, option);
                }
                if (mSelectPosition == 0) {
                        setSelectPosition(0);
                }
        }

        public int getSelectPosition() {
                return mSelectPosition;
        }

        public void setSelectPosition(int selectPosition) {
                mSelectPosition = selectPosition;
                if (mContents == null || selectPosition < 0 || selectPosition >= mContents.size()) {
                        return;
                }
                mResult.setText(mContents.get(selectPosition));
                if (mListener != null) {
                        mListener.onChange(selectPosition, mContents.get(selectPosition));
                }
        }

        public void setDefault(int aDefault) {
                mDefault = aDefault;
        }

        public String getSelectedItem() {
                if (mSelectPosition >= 0 && mContents != null && mSelectPosition < mContents.size()) {
                        return mContents.get(mSelectPosition);
                }
                return null;
        }

        public OnSelectChangeListener getListener() {
                return mListener;
        }

        public void setListener(OnSelectChangeListener listener) {
                mListener = listener;
        }

        private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
                final TypedArray a = getContext().obtainStyledAttributes(
                        attrs, R.styleable.SettingSelectView, defStyleAttr, 0);
                CharSequence[] entries = a.getTextArray(R.styleable.SettingSelectView_optionsEntry);
                if (entries != null && entries.length > 0) {
                        mContents = new ArrayList<>();
                        for (CharSequence entry : entries) {
                                mContents.add(entry.toString());
                        }
                }
                mTitle = a.getString(R.styleable.SettingSelectView_optionsTitle);
                mIcon = a.getDrawable(R.styleable.SettingSelectView_optionsIcon);
                mDefault = a.getInt(R.styleable.SettingSelectView_optionsDefault, 0);
                mSelectPosition = mDefault;

                View rootView = View.inflate(context, R.layout.layout_setting_select, this);
                TextView tvTitle = rootView.findViewById(R.id.title);
                tvTitle.setText(mTitle);
                mResult = rootView.findViewById(R.id.result);
                mResult.setText(getSelectedItem());
                ImageView ivIcon = rootView.findViewById(R.id.icon);
                if (mIcon != null) {
                        ivIcon.setImageDrawable(mIcon);
                }
                rootView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                if (mContents == null || mContents.size() == 0) {
                                        ToastUtils.showShort(R.string.no_options);
                                } else {
                                        LovelyChoiceDialog dialog = new LovelyChoiceDialog(context)
                                                .setTopColorRes(R.color.accent)
                                                .setTitle(mTitle)
                                                .setItems(contentsAddDefault(context), new LovelyChoiceDialog.OnItemSelectedListener<String>() {
                                                        @Override
                                                        public void onItemSelected(int position, String item) {
                                                                mSelectPosition = position;
                                                                String option = mContents.get(position);
                                                                mResult.setText(option);
                                                                if (mListener != null) {
                                                                        mListener.onChange(position, option);
                                                                }
                                                        }
                                                });
                                        if (mIcon != null) {
                                                dialog.setIcon(mIcon);
                                                dialog.show();
                                        }

                                }
                        }
                });
        }

        private List<String> contentsAddDefault(Context context) {
                if (mContents == null) {
                        return null;
                }
                ArrayList<String> result = new ArrayList<>(mContents.size());
                for (int i = 0; i < mContents.size(); i++) {
                        if (i == mDefault) {
                                result.add(mContents.get(i) + context.getString(R.string.default_option));
                        } else {
                                result.add(mContents.get(i));
                        }
                }
                return result;
        }

        public interface OnSelectChangeListener {
                void onChange(int position, String param);
        }
}
