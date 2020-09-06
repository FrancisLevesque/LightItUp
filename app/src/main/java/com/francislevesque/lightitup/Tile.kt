package com.francislevesque.lightitup

import android.graphics.RectF

class Tile(column: Int, row: Int, screenWidth: Int) {
    // Create space for required tiles plus an extra
    // The extra space is divided and used for padding between tiles
    val numUnits = LightUpView.tileRows + 1
    val unitLength = screenWidth / numUnits
    val padding = unitLength / numUnits

    private val left = (padding * column) + (unitLength * (column - 1)).toFloat()
    private val top = ((padding + unitLength) * row).toFloat()
    private val right = ((padding + unitLength) * column).toFloat()
    private val bottom = (((padding + unitLength) * row) + unitLength).toFloat()
    var position = RectF(left, top, right, bottom)

    var isLightOn = false

    fun update() {
        isLightOn = !isLightOn
    }
}