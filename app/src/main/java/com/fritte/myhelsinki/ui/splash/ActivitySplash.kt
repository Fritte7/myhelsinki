package com.fritte.myhelsinki.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.fritte.myhelsinki.R
import com.fritte.myhelsinki.ui.map.ActivityMap

class ActivitySplash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            startActivity(Intent(this, ActivityMap::class.java))
            finish()
        }, 1000)
    }
}