package com.francislevesque.lightitup

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceView

class LightUpView(context: Context, val size: Point) : SurfaceView(context), Runnable {
    private val gameThread = Thread(this)
    private var canvas: Canvas = Canvas()
    private var paint: Paint = Paint()
    private val offColour = Color.argb(255, 80, 80, 80)
    private val onColour = Color.argb(255, 200, 200, 0)
    private var gameWon = false

    private val tiles = ArrayList<Tile>()

    companion object {
        val tileRows = 5
        val tileColumns = 5
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_POINTER_UP,
            MotionEvent.ACTION_UP -> {
                for (tile in tiles) {
                    if (event.x > tile.position.left &&
                        event.x < tile.position.right &&
                        event.y > tile.position.top &&
                        event.y < tile.position.bottom) {
                        tile.update()
                        break
                    }
                }
            }
        }
        return true
    }

    private fun setupGame() {
        for (column in 1..tileColumns) {
            for (row in 1..tileRows) {
                tiles.add(Tile(column, row, size.x))
            }
        }
    }

    private fun update() {
        if (gameWon) {
            // show a win screen or something
        }
    }

    private fun draw() {
        if (holder.surface.isValid) {
            canvas = holder.lockCanvas()
            canvas.drawColor(Color.argb(255, 0, 0, 0))
            paint.color = Color.argb(255, 80, 80, 80)

            for (tile in tiles) {
                if (tile.isLightOn) {
                    paint.color = onColour
                } else {
                    paint.color = offColour
                }
                canvas.drawRect(tile.position, paint)
            }
            holder.unlockCanvasAndPost(canvas)
        }
    }

    override fun run() {
        while(true) {
            update()
            draw()
        }
    }

    fun pause() {
        try {
            gameThread.join()
        }  catch (e: InterruptedException) {
            Log.e("ERROR:", "Failure joining game thread")
        }
    }

    fun resume() {
        setupGame()
        gameThread.start()
    }
}