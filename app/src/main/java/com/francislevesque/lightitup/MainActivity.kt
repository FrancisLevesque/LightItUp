package com.francislevesque.lightitup

import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    private lateinit var lightUpView: LightUpView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val display = windowManager.defaultDisplay
        var size = Point()
        display.getSize(size)

        lightUpView = LightUpView(this, size)
        setContentView(lightUpView)
    }

    override fun onPause() {
        super.onPause()
        lightUpView.pause()
    }

    override fun onResume() {
        super.onResume()
        lightUpView.resume()
    }
}