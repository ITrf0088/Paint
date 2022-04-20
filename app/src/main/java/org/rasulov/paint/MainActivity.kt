package org.rasulov.paint

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    private lateinit var drawing: Drawing
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawing = findViewById(R.id.drawing)
        drawing.setPaintStyle(Color.BLACK, 20f)
        drawing.setPaintStyle(Color.BLACK, 20f)
    }
}