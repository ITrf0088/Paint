package org.rasulov.paint.drawingview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.text.style.BackgroundColorSpan
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class Drawing(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var oldX: Float = 0f
    private var oldY: Float = 0f


    private var color: Int = Color.RED
    private var thickness: Float = 0f

    private var pathPaint: PathPaint = PathPaint(color, thickness)
    private val pathPaints: ArrayList<PathPaint> = ArrayList()

    init {
        setBrushSize(5f)
    }

    fun setColor(color: Color) {
        this.color = Color.parseColor(color.toString())
    }

    fun setColor(color: Any?) {
        this.color = Color.parseColor(color.toString())
    }

    fun getColor(): Int = color

    fun setBrushSize(brushSize: Float) {
        this.thickness = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            brushSize,
            resources.displayMetrics
        )
    }


    fun undo() {
        if (pathPaints.size > 0) {
            pathPaints.removeAt(pathPaints.size - 1)
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        pathPaints.forEach { canvas.drawPath(it.path, it.paint) }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val element = PathPaint(color, thickness)
                pathPaints.add(element)
                pathPaint = element
                pathPaint.path.moveTo(x, y)
                oldX = x
                oldY = y
            }
            MotionEvent.ACTION_MOVE -> smoothLine(x, y)
            MotionEvent.ACTION_UP -> pathPaint.path.lineTo(x, y)
            else -> return false
        }

        invalidate()
        return true

    }

    private fun smoothLine(x: Float, y: Float) {

        val halfX = abs((oldX + x) / 2)
        val halfY = abs((oldY + y) / 2)

        pathPaint.path.quadTo(oldX, oldY, halfX, halfY)

        oldX = x
        oldY = y
    }


    fun getDrawn(drawable: Drawable?): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val bitmapCanvas = Canvas(bitmap)
        if (drawable != null) {
            drawable.draw(bitmapCanvas)
        } else {
            bitmapCanvas.drawColor(Color.WHITE)
        }
        pathPaints.forEach {
            bitmapCanvas.drawPath(it.path, it.paint)
        }

        return bitmap
    }

}