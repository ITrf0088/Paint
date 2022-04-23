package org.rasulov.paint

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.core.app.DialogCompat
import androidx.core.content.ContextCompat
import org.rasulov.paint.drawingview.Drawing

class MainActivity : AppCompatActivity() {

    private lateinit var drawing: Drawing
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawing = findViewById(R.id.drawing)
        val imgBtn = findViewById<ImageButton>(R.id.img_btn_select_b_size)
        imgBtn.setOnClickListener { showDialog() }

    }


    private fun showDialog() {

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_brush_size)
        val small = DialogCompat.requireViewById(dialog, R.id.img_btn_small_b_size)
        val medium = DialogCompat.requireViewById(dialog, R.id.img_btn_medium_b_size)
        val large = DialogCompat.requireViewById(dialog, R.id.img_btn_large_b_size)
        setUpImgBtn(dialog, small, 5f)
        setUpImgBtn(dialog, medium, 10f)
        setUpImgBtn(dialog, large, 15f)

        dialog.show()
    }

    private fun setUpImgBtn(dialog: Dialog, view: View, size: Float) {
        view.setOnClickListener { drawing.setBrushSize(size);dialog.dismiss() }
        val drawable = (view as ImageButton).drawable
        if (drawable is GradientDrawable) {
            drawable.setColor(drawing.color)
        }

    }

}