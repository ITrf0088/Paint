package org.rasulov.paint

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.math.log

class Drawing(context: Context, attrs: AttributeSet) : View(context, attrs) {


    private lateinit var bitmap: Bitmap
    private lateinit var bitmapCanvas: Canvas
    private var pathPaint: PathPaint = PathPaint()
    private val pathPaints: ArrayList<PathPaint> = ArrayList()


    fun setPaintStyle(color: Int, brushSize: Float) {
        val temp = PathPaint(color, brushSize)
        val indexOf = pathPaints.indexOf(temp)
        this.pathPaint =
            if (indexOf != -1) {
                Log.d("it0088", "+")
                pathPaints[indexOf]
            } else {
                pathPaints.add(temp)
                Log.d("it0088", "-")
                temp
            }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        val path = pathPaint.path
        val paint = pathPaint.paint

        if (!path.isEmpty) {
            canvas.drawPath(path, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        val path = pathPaint.path

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(x, y)
                path.lineTo(x, y)
            }
            MotionEvent.ACTION_MOVE -> path.lineTo(x, y)
            else -> return false
        }

        invalidate()
        return true

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bitmapCanvas = Canvas(bitmap)
        Log.d("it0088", "onSizeChanged: $w $h")
    }
}