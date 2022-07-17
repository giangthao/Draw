package com.tutorial.draw

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.MediaStore
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get

class MainActivity : AppCompatActivity() {
    private  var drawingView:DrawingView? = null
    private var mImageButtonCurrentPaint: ImageButton? = null
    private var mImageButtonImage: ImageButton? = null
    val openGalleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
            if(result.resultCode== RESULT_OK && result.data!=null){
                val imageBackground: ImageView = findViewById(R.id.iv_background)
                imageBackground.setImageURI(result.data?.data)
            }
        }

    val requestPremission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ){
                permissions-> Log.d("MainActivity","Permissions $permissions")
            permissions.entries.forEach{
                val permissionName = it.key
                val isGranted = it.value
                if(isGranted){
                    if ( permissionName == Manifest.permission.READ_EXTERNAL_STORAGE) {
                        Toast.makeText(
                            this@MainActivity,
                            "Permission granted now you can read the storage files.",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        val pickIntent = Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            )
                        openGalleryLauncher.launch(pickIntent)

                } else {
                    if(permissionName==Manifest.permission.READ_EXTERNAL_STORAGE){
                        Toast.makeText(
                            this,
                            "Permission denied for Folder",
                            Toast.LENGTH_LONG)
                            .show()
                    }
                    }
            }
        }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawingView = findViewById(R.id.drawing_view)
        drawingView?.setSizeForBrush(20.toFloat())

        val linearLayoutPaintColor = findViewById<LinearLayout>(R.id.ll_paint_collors)
        mImageButtonCurrentPaint = linearLayoutPaintColor[2] as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this,R.drawable.pallet_presed)
        )
        val ib_brush :ImageButton = findViewById(R.id.ib_brush)
        ib_brush.setOnClickListener{
            showBrushSizeChooserDialog()
        }
        val mImageButton : ImageButton = findViewById(R.id.ib_gallery)
        /*mImageButton.setOnClickListener {
            if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)
            ){
                showRationaleDialog("Permission Storage",
                "Storage cannot be used because Storage access is denied"
                    )
            } else {
                requestPremission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            }
        }*/
        mImageButton.setOnClickListener {
            requestStoragePermission()
        }
    }
    private  fun showBrushSizeChooserDialog(){
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush Size")
        val smallBtn : ImageButton = brushDialog.findViewById(R.id.ib_small_brush)
        smallBtn.setOnClickListener{
            drawingView?.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()
        }

        val mediumBtn : ImageButton = brushDialog.findViewById(R.id.ib_medium_brush)
        mediumBtn.setOnClickListener(View.OnClickListener {
            drawingView?.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        }
        )
        val largeBtn : ImageButton = brushDialog.findViewById(R.id.ib_large_brush)
        largeBtn.setOnClickListener(View.OnClickListener {
            drawingView?.setSizeForBrush(30.toFloat())
            brushDialog.dismiss()
        }
        )
        brushDialog.show()
    }
    fun paintClicked(view: View){
        Toast.makeText(this,"clicked paint",Toast.LENGTH_LONG)
        if(view!==mImageButtonCurrentPaint){
            val imageButton = view as ImageButton
            val colorTag = imageButton.tag.toString()
            drawingView?.setColor(colorTag)
            imageButton.setImageDrawable(
                ContextCompat.getDrawable(this,R.drawable.pallet_presed)
            )
            mImageButtonCurrentPaint?.setImageDrawable(
                ContextCompat.getDrawable(this,R.drawable.pallet_normal)
            )
            mImageButtonCurrentPaint = view

        }
    }
    private fun requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
        )){
            showRationaleDialog("Kids Drawing App","Kids Drawing App"
            +" needs to Access Your External Storage"
            )
            }
        else{
            requestPremission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            // TODO - Add writing external storage
        }

    }
    private fun showRationaleDialog(
        title: String,
        message: String
    ){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Cancel"){
                dialog,_-> dialog.dismiss()
            }
        builder.create().show()

    }
}