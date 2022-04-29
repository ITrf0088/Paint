package org.rasulov.paint.drawingview

import android.content.Context
import android.graphics.*
import android.text.style.BackgroundColorSpan
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class Drawing(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private lateinit var bitmap: Bitmap
    private lateinit var bitmapCanvas: Canvas
    private val bitmapPaint: Paint = Paint(Paint.DITHER_FLAG)

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

    fun setBackground(background: Bitmap) {
        bitmap = background
        bitmapCanvas = Canvas(bitmap)
        invalidate()

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