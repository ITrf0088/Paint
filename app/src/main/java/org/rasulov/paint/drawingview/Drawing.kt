package org.rasulov.paint.drawingview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class Drawing(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private lateinit var bitmap: Bitmap
    private lateinit var bitmapCanvas: Canvas

    var color: Int = Color.RED
    private var thickness: Float = 0f
    private val bitmapPaint: Paint = Paint(Paint.DITHER_FLAG)
    private var pathPaint: PathPaint = PathPaint(color, thickness)
    private val pathPaints: ArrayList<PathPaint> = ArrayList()

    init {
        setBrushSize(5f)
    }

    fun setBrushSize(brushSize: Float) {
        this.thickness = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            brushSize,
            resources.displayMetrics
        )
    }

    fun setPaintColor(color: Int) {
        this.color = color
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        bitmapCanvas.drawColor(Color.WHITE)
        for (p in pathPaints) {
            val path = p.path
            val paint = p.paint
            bitmapCanvas.drawPath(path, paint)
        }

        canvas.drawBitmap(bitmap, 0f, 0f, bitmapPaint)
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
                pathPaint.path.lineTo(x, y)
            }
            MotionEvent.ACTION_MOVE -> pathPaint.path.lineTo(x, y)
            else -> return false
        }

        invalidate()
        return true

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bitmapCanvas = Canvas(bitmap)
    }
}