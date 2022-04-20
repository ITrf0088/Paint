package org.rasulov.paint

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path

class PathPaint(val color: Int = Color.BLACK, val thickness: Float = 10f) {


    val paint: Paint = Paint()
    val path: Path = Path()

    init {
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.color = color
        paint.strokeWidth = thickness
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PathPaint) return false
        if (hashCode() == other.hashCode()) {
            return color == other.color && thickness == other.thickness
        }
        return false
    }

    override fun hashCode(): Int {
        return 31 * color.hashCode() + thickness.hashCode()
    }

}