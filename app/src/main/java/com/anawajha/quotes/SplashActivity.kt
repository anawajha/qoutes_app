package com.anawajha.quotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatDelegate

class SplashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        if (loadState() == true){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            setTheme(R.style.Theme_Quotes);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            setTheme(R.style.Theme_Quotes);
        }

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)
        val handler = Handler()
        handler.postDelayed({startActivity(Intent(this,MainActivity::class.java))
                            finish()},2500L)
    }

    private fun loadState(): Boolean? {
        val sharedPreferences = getSharedPreferences("Quotes", MODE_PRIVATE)
        return sharedPreferences.getBoolean("NightMode", false)
    }

}