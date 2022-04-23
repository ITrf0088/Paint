package org.rasulov.paint.drawingview

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path

class PathPaint(color: Int = Color.BLACK, thickness: Float = 10f) {


    val paint: Paint = Paint()
    val path: Path = Path()

    init {
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.color = color
        paint.strokeWidth = thickness
        paint.isAntiAlias = true
        paint.isDither = true
    }


}