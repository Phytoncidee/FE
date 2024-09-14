package com.phytoncidee.hikinglog_fe

import android.app.Application
import android.util.Log
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.sdk.common.util.Utility

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        var keyHash = Utility.getKeyHash(this)
        Log.i("MyApplication", "$keyHash")


        KakaoMapSdk.init(this, KAKAO_API_KEY)
    }
}
