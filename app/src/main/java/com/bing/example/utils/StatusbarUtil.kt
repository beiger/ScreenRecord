package com.bing.example.utils

import android.content.Context
import android.os.Build
import java.lang.reflect.Method

/**
 *
 * 收起通知栏
 * @param context
 */
fun collapseStatusBar(context: Context) {
        try {
                val statusBarManager = context.getSystemService("statusbar")
                val collapse: Method
                if (Build.VERSION.SDK_INT <= 16) {
                        collapse = statusBarManager.javaClass.getMethod("collapse");
                } else {
                        collapse = statusBarManager::class.java.getMethod("collapsePanels");
                }
                collapse.isAccessible = true
                collapse.invoke(statusBarManager);
        } catch (localException: Exception) {
                localException.printStackTrace()
        }
}