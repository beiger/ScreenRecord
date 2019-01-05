package com.bing.example.main.service

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.*
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit
import android.view.WindowManager
import com.bing.example.R

class CountDownManager(val context: Context) {
        private val OVERLAY_TYPE: Int = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
                WindowManager.LayoutParams.TYPE_PRIORITY_PHONE
        } else {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }
        private val windowManager: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        private val viewList: MutableList<View> = mutableListOf()

        fun startCountDown(number: Int, start: () -> Unit, callback: () -> Unit) {
                val inflater: LayoutInflater = LayoutInflater.from(context)
                val textView = inflater.inflate(R.layout.layout_count_down, null) as TextView
                val params = WindowManager.LayoutParams()
                params.width= WRAP_CONTENT
                params.height  = WRAP_CONTENT
                params.type = OVERLAY_TYPE
                params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                params.format = PixelFormat.TRANSLUCENT
                params.gravity = Gravity.CENTER
                windowManager.addView(textView, params)
                viewList.add(textView)

                var animate: ViewPropertyAnimator? = null

                start()

                val disposable = Observable
                        .interval(0, 1000, TimeUnit.MILLISECONDS)
                        .take(number.toLong() + 1)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                                val result = number - it!!
                                if (result <= 0L) {
                                        removeAllViews()
                                        callback()
                                        return@subscribe
                                }
                                textView.text = (result).toString()
                                animate?.cancel()
                                textView.alpha = 1f
                                animate = textView.animate().alpha(0f).setDuration(1000)
                        }
        }

        private fun removeAllViews() {
                viewList.forEach {
                        windowManager.removeViewImmediate(it)
                }
                viewList.clear()
        }
}