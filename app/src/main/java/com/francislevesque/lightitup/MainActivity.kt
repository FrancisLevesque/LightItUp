package com.francislevesque.lightitup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainButton3.setOnClickListener { levelSelect(3) }
        mainButton4.setOnClickListener { levelSelect(4) }
        mainButton5.setOnClickListener { levelSelect(5) }
        mainButton6.setOnClickListener { levelSelect(6) }
    }

    private fun levelSelect(size: Int) {
        val gameIntent = Intent(this, GameActivity::class.java).apply {
            putExtra(EXTRA_LEVEL_SIZE, size)
        }
        startActivity(gameIntent)
    }
}