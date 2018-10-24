package com.bing.example.main.bindingadapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

import androidx.databinding.BindingAdapter;

public class BindingAdapters {

	@BindingAdapter("img_url")
	public static void showImg(ImageView iv, String url) {
		Glide.with(iv).load(url).into(iv);
	}
}
