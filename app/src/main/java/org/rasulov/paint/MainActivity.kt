package org.rasulov.paint

import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.DialogCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.children
import org.rasulov.paint.drawingview.Drawing

class MainActivity : AppCompatActivity() {

    private lateinit var drawing: Drawing
    private lateinit var linearPallet: LinearLayout
    private lateinit var frame: FrameLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawing = findViewById(R.id.drawing)
        linearPallet = findViewById(R.id.linear_pallet)
        frame = findViewById(R.id.frame)
        val imgBtnSelectBrushSize = findViewById<ImageButton>(R.id.img_btn_select_b_size)
        val imgBtnImageChooser = findViewById<ImageButton>(R.id.img_btn_chooser_image)
        val imgBtnImageUndo = findViewById<ImageButton>(R.id.img_btn_undo)

        imgBtnSelectBrushSize.setOnClickListener { showDialog() }
        imgBtnImageChooser.setOnClickListener { chooseImage() }
        imgBtnImageUndo.setOnClickListener { drawing.undo() }
        setPalette()
        setListeners()

    }


    private fun setListeners() {
        linearPallet.children.forEach { it ->
            it.setOnClickListener {
                drawing.setColor(it.tag)
                setPalette()
            }
        }
    }

    private fun setPalette() {
        linearPallet.children.forEach {
            val parseColor = Color.parseColor(it.tag.toString())
            var drawable = ContextCompat.getDrawable(this, R.drawable.pallet_normal)?.mutate()
            if (parseColor == drawing.getColor())
                drawable = ContextCompat.getDrawable(this, R.drawable.pallet_selected)

            if (drawable is GradientDrawable) {
                drawable.setColor(parseColor)
                it.background = drawable
            }

        }
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
            drawable.setColor(drawing.getColor())
        }

    }


    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        launcher.launch(intent)

    }

    private fun showRationaleDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Permission request!")
            .setMessage("Would you like to use an image from Gallery")
            .setPositiveButton("Yes") { _, _ -> }
            .setNegativeButton("No, thanks") { d, _ -> d.dismiss() }
    }


    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val data = it.data?.data
                if (data != null) {
                    val stream = contentResolver.openInputStream(data)
                    val decodeBitmap = BitmapFactory.decodeStream(stream)
                    frame.background = decodeBitmap.toDrawable(resources)
                }
            }
        }

}


