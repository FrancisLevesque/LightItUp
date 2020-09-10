package com.francislevesque.lightitup

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceView
import kotlin.random.Random

class LightUpView(context: Context, val screenSize: Point, val gameSize: Int) : SurfaceView(context), Runnable {
    private val gameThread = Thread(this)
    private var canvas: Canvas = Canvas()
    private var paint: Paint = Paint()
    private val offColour = Color.argb(255, 80, 80, 80)
    private val onColour = Color.argb(255, 200, 200, 0)
    private var gameOn = false
    private var gameWon = false
    private var bestScore = 0
    private var moveCounter = 0
    private var allTilesOn = false

    private val textX = screenSize.x / 20f
    private val textY = screenSize.y / 20f
    private val textSize = 60f

    private lateinit var tiles: Array<Array<Tile>>

    private fun setupGame() {
        gameWon = false
        moveCounter = 0
        allTilesOn = false

        val chanceList = (0..2017)
        tiles = Array(gameSize) { row ->
            Array(gameSize) { column ->
                val chance = (chanceList.random() % 3 == 0)
                Tile(chance, gameSize, row, column, screenSize.x, screenSize.y)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_POINTER_UP,
            MotionEvent.ACTION_UP -> {
                moveCounter++
                for (tileRow in tiles) {
                    for (tile in tileRow) {
                        val row = tile.row
                        val column = tile.column

                        if (event.x > tile.position.left &&
                            event.x < tile.position.right &&
                            event.y > tile.position.top &&
                            event.y < tile.position.bottom) {
                            tile.update()

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
                            break
                        }
                    }
                }
                allTilesOn = true
                for (tileRow in tiles) {
                    for (tile in tileRow) {
                        if(!tile.isLightOn) {
                            allTilesOn = false
                        }
                    }
                }

                if (allTilesOn) {
                    gameOn = false
                    gameWon = true
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
            canvas.drawText("Best Score: $bestScore", textX, textY, paint)
            canvas.drawText("Number of moves used: $moveCounter", textX, textY + textSize + 20f, paint)
            if (gameWon) {
                paint.textSize = 80f
                canvas.drawText("YOU WIN!", screenSize.x / 2f, screenSize.y / 2f, paint)
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