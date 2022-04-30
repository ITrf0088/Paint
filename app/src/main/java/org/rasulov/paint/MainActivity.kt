package org.rasulov.paint

import android.Manifest
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.DialogCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.children
import org.rasulov.paint.drawingview.Drawing
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    private lateinit var drawing: Drawing
    private lateinit var linearPallet: LinearLayout
    private lateinit var frame: FrameLayout
    private lateinit var progressDialog: Dialog
    private var task: Async? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawing = findViewById(R.id.drawing)
        linearPallet = findViewById(R.id.linear_pallet)
        frame = findViewById(R.id.frame)
        task = Async()
        val imgBtnSelectBrushSize = findViewById<ImageButton>(R.id.img_btn_select_b_size)
        val imgBtnImageChooser = findViewById<ImageButton>(R.id.img_btn_chooser_image)
        val imgBtnImageUndo = findViewById<ImageButton>(R.id.img_btn_undo)
        val imgBtnImageSave = findViewById<ImageButton>(R.id.img_btn_save)
        imgBtnSelectBrushSize.setOnClickListener { showDialog() }
        imgBtnImageChooser.setOnClickListener { chooseImage() }
        imgBtnImageUndo.setOnClickListener { drawing.undo() }
        imgBtnImageSave.setOnClickListener { save() }
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


    private fun save() {
        var out: OutputStream? = null

        val filename = "${System.currentTimeMillis()}.png"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            val insert =
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            out = insert?.let { contentResolver.openOutputStream(insert) }

        } else {
            val checked =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (checked == PackageManager.PERMISSION_GRANTED) {
                val imagesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val image = File(imagesDir, filename)
                out = FileOutputStream(image)
            } else {
                requestWritePermission()
            }
        }
        progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.progressbar)
        progressDialog.show()
        task = Async()
        task?.execute(out)

    }

    private fun requestWritePermission() {
        val rationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (rationale) {
            showRationaleDialog()
        } else {
            requestWrite.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun showRationaleDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Permission request!")
            .setMessage("it's needed the permission to save an image to Gallery.")
            .setPositiveButton("Yes") { _, _ -> requestWrite.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE) }
            .setNegativeButton("No, thanks") { d, _ -> d.dismiss() }
        dialog.show()

    }


    inner class Async : AsyncTask<OutputStream, Unit, Boolean>() {


        override fun doInBackground(vararg outs: OutputStream?): Boolean {
            try {
                outs[0]?.use {
                    val drawnAsBitmap = drawing.getDrawn(frame.background)
                    drawnAsBitmap.compress(Bitmap.CompressFormat.PNG, 90, it)
                }
            } catch (e: Exception) {
                return false
            }
            return true
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            progressDialog.dismiss()
            var string = ""
            string = if (result == true) "Successfully" else "Something went wrong!"
            Toast.makeText(this@MainActivity, string, Toast.LENGTH_SHORT).show()
        }
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

    private val requestWrite =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            Log.d("it0088", "$granted: ")
            if (granted) save()
        }

    override fun onDestroy() {
        super.onDestroy()
        task?.let {
            if (!it.isCancelled) {
                it.cancel(true)
            }
        }
    }
}


