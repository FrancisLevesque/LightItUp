package com.francislevesque.lightitup

import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class GameActivity : AppCompatActivity() {
    private lateinit var lightUpView: LightUpView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val display = windowManager.defaultDisplay
        var screenSize = Point()
        display.getSize(screenSize)
        val gameSize = intent.getIntExtra(EXTRA_LEVEL_SIZE, 3)

        lightUpView = LightUpView(this, screenSize, gameSize)
        setContentView(lightUpView)
    }

    override fun onResume() {
        super.onResume()
        lightUpView.resume()
    }

    override fun onPause() {
        super.onPause()
        lightUpView.pause()
    }
}