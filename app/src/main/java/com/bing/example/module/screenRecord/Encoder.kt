package com.bing.example.module.screenRecord

import java.io.IOException

interface Encoder {
        @Throws(IOException::class)
        fun prepare()

        fun stop()

        fun release()

        fun setCallback(callback: Callback)

        interface Callback {
                fun onError(encoder: Encoder, exception: Exception)
        }
}
