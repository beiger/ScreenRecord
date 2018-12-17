package com.bing.example.main.bindingadapter

import android.widget.ImageView

import com.bumptech.glide.Glide

import androidx.databinding.BindingAdapter

@BindingAdapter("img_url")
fun showImg(iv: ImageView, url: String) {
        Glide.with(iv).load(url).into(iv)
}
