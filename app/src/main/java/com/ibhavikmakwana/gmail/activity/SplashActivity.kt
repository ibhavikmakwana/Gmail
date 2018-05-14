package com.ibhavikmakwana.gmail.activity

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.ibhavikmakwana.gmail.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            MainActivity.launchActivity(this)
            finish()
        }, 1500)
    }
}
