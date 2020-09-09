package com.francislevesque.lightitup

import android.graphics.RectF

class Tile(val gameSize: Int, val row: Int, val column: Int, screenWidth: Int, screenHeight: Int) {
    // Create space for required tiles plus an extra
    // The extra space is divided and used for padding between tiles
    val numUnits = gameSize + 1
    val unitLength = screenWidth / numUnits
    val padding = unitLength / numUnits
    // TODO: Add and enforce check to make sure we're not drawing off the screen
    val topSpace = screenHeight / 6

    private val left = (padding + ((unitLength + padding) * column)).toFloat()
    private val top = (((padding + unitLength) * row) + topSpace).toFloat()
    private val right = (padding + unitLength + ((padding + unitLength) * column)).toFloat()
    private val bottom = (((padding + unitLength) * row) + unitLength + topSpace).toFloat()
    var position = RectF(left, top, right, bottom)

    var isLightOn = false

    fun update() {
        isLightOn = !isLightOn
    }
}