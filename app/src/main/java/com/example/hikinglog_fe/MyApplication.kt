package com.example.hikinglog_fe

import android.app.Application
import android.util.Log
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.sdk.common.util.Utility

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        var keyHash = Utility.getKeyHash(this)
        Log.i("GlobalApplication", "$keyHash")

        KakaoMapSdk.init(this, "b054d4aac052e2c90ddc6fee33f06119")
    }
}
