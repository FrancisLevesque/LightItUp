package com.francislevesque.lightitup

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceView

class LightUpView(context: Context, val screenSize: Point, val gameSize: Int) : SurfaceView(context), Runnable {
    private val gameThread = Thread(this)
    private var canvas: Canvas = Canvas()
    private var paint: Paint = Paint()
    private val offColour = Color.argb(255, 80, 80, 80)
    private val onColour = Color.argb(255, 200, 200, 0)
    private val textX = screenSize.x / 20f
    private val textY = screenSize.y / 20f
    private val textSize = 40f

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "LightItUp", Context.MODE_PRIVATE
    )
    private var gameOn = false
    private var gameWon = false
    private var allTilesOn = false
    private var moveCounter = 0
    private var maxScore = 9999
    private var currentRecordKey = "currentRecord$gameSize"
    private var currentRecord = prefs.getInt(currentRecordKey, maxScore)

    private lateinit var tiles: Array<Array<Tile>>

    private fun setupGame() {
        gameWon = false
        moveCounter = 0
        allTilesOn = false

        tiles = Array(gameSize) { row ->
            Array(gameSize) { column ->
                Tile(true, gameSize, row, column, screenSize.x, screenSize.y)
            }
        }
        val numberOfMoves = (gameSize..(2*gameSize))
        val gameRange = (0..(gameSize - 1))
        val xPosition = gameRange
        val yPosition = gameRange
        for (move in 0..numberOfMoves.random()) {
            touchTile(xPosition.random(), yPosition.random())
        }
    }

    private fun touchTile(row: Int, column: Int) {
        tiles[row][column].update()
        if (row > 0) {
            tiles[row - 1][column].update()
        }
        if (row < gameSize - 1) {
            tiles[row + 1][column].update()
        }
        if (column > 0) {
            tiles[row][column - 1].update()
        }
        if (column < gameSize - 1) {
            tiles[row][column + 1].update()
        }
    }

    private fun updateTileIfTouched(x: Float, y: Float) {
        for (tileRow in tiles) {
            for (tile in tileRow) {
                if (x > tile.position.left &&
                    x < tile.position.right &&
                    y > tile.position.top &&
                    y < tile.position.bottom
                ) {
                    moveCounter++
                    touchTile(tile.row, tile.column)
                    break
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_POINTER_UP,
            MotionEvent.ACTION_UP -> {
                updateTileIfTouched(event.x, event.y)
                allTilesOn = true
                for (tileRow in tiles) {
                    for (tile in tileRow) {
                        if(!tile.isLightOn) {
                            allTilesOn = false
                        }
                    }
                }

                if (allTilesOn) {
                    gameWon = true
                    if (moveCounter < currentRecord) {
                        val editor = prefs.edit()
                        editor.putInt(currentRecordKey, moveCounter)
                        editor.apply()
                    }
                    gameOn = false
                }
            }
        }
        return true
    }

    private fun draw() {
        if (holder.surface.isValid) {
            canvas = holder.lockCanvas()
            canvas.drawColor(Color.argb(255, 0, 0, 0))
            for (tileRow in tiles) {
                for (tile in tileRow) {
                    val tile = tiles[tile.row][tile.column]
                    if (tile.isLightOn) {
                        paint.color = onColour
                    } else {
                        paint.color = offColour
                    }
                    canvas.drawRect(tile.position, paint)
                }
            }
            paint.color = Color.argb(255, 255, 255, 255)
            paint.textSize = textSize
            if (currentRecord != maxScore) {
                canvas.drawText("Current Record: $currentRecord", textX, textY, paint)
            }
            canvas.drawText("Number of moves used: $moveCounter", textX, textY + textSize + 20f, paint)
            if (gameWon) {
                paint.textSize = 120f
                paint.textAlign = Paint.Align.CENTER
                canvas.drawText("YOU WIN!", screenSize.x / 2f, (screenSize.y / 8f) * 7f, paint)
            }
            holder.unlockCanvasAndPost(canvas)
        }
    }

    override fun run() {
        while(gameOn) {
            draw()
        }
    }

    fun resume() {
        setupGame()
        gameOn = true
        gameThread.start()
    }

    fun pause() {
        gameOn = false
        try {
            gameThread.join()
        }  catch (e: InterruptedException) {
            Log.e("ERROR:", "Failure joining game thread")
        }
    }
}