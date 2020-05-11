package com.example.lazyvision

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.lazyvision.ProgressBar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import id.zelory.compressor.Compressor.compress
import id.zelory.compressor.constraint.*
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis


private const val REQUEST_CODE_PERMISSIONS = 10

private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

class CameraActivity : AppCompatActivity() {

    private lateinit var button: Button
    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var viewFinder: TextureView
    private lateinit var mycase: TextView
    private lateinit var mbitmap: Bitmap

    internal var downloadingurl:String?=null
    internal  var storage:FirebaseStorage?=null
    internal  var storageReference:StorageReference?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        storage= FirebaseStorage.getInstance()
        storageReference=storage!!.reference
        viewFinder = findViewById(R.id.view_finder)
        mycase = findViewById(R.id.mycase)
        button = findViewById(R.id.button)
        val id=intent.getStringExtra("id")
        val mycaseno = intent.getStringExtra("caseno")
        mycase.setText(mycaseno.toString())
        label.setText(id.toString())

        if (allPermissionsGranted()) {
            viewFinder.post { startCamera() }
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        viewFinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateTransform()
        }

        button.setOnClickListener(){
            val imageUri : Uri = Uri.fromFile(filex)
            mbitmap=MediaStore.Images.Media.getBitmap(contentResolver,imageUri)
            var compressedbitmap=compressBitmap(mbitmap,10)
            Log.e("BUTMAP......................",compressedbitmap.toString())

            val IMGURI=getImageUriFromBitmap(applicationContext,compressedbitmap)

                var path="images/"+UUID.randomUUID().toString()
                var fileRef=storageReference!!.child(path)
                val uploadTask = fileRef.putFile(IMGURI!!)
                uploadTask.continueWith {
                    if (!it.isSuccessful) {
                        it.exception?.let { t ->
                            throw t
                        }
                    }

                    fileRef.downloadUrl
                }.addOnCompleteListener {
                    if (it.isSuccessful) {
                        it.result!!.addOnSuccessListener { task ->
                            downloadingurl = task.toString()



                if(id.toString() == "objectVision"){
                    val intent = Intent(this@CameraActivity,ObjectVision::class.java)
                    intent.putExtra("imageUri", imageUri.toString())
                    intent.putExtra("caseno",mycaseno.toString())
                    intent.putExtra("imageUrl",downloadingurl.toString())

                    startActivity(intent)
                }
                else if(id.toString() == "textVision") {
                    val intent = Intent(this@CameraActivity, TextVision::class.java)
                    // This method will be executed once the timer is over
                    intent.putExtra("imageUri", imageUri.toString());
                    intent.putExtra("caseno", mycaseno.toString())
                    intent.putExtra(
                        "imageUrl",
                               downloadingurl.toString()
                    )
                    startActivity(intent)


                }else if(id.toString() == "labelVision"){
                    val intent = Intent(this@CameraActivity,LabelVision::class.java)
                    intent.putExtra("imageUri", imageUri.toString());
                    intent.putExtra("caseno",mycaseno.toString())
                    intent.putExtra("imageUrl",downloadingurl)
                    startActivity(intent)
                }else{
                    Toast.makeText(baseContext, "Wrong Option Selected", Toast.LENGTH_SHORT).show()
                }
                finish()

                        }
                    }
                }

        }

    }


    lateinit var filex: File
    fun setText(file: File){
        this.filex = file
    }




    private fun startCamera() {

        // Create configuration object for the viewfinder use case
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetResolution(Size(640, 480))
        }.build()


        // Build the viewfinder use case
        val preview = Preview(previewConfig)

        // Every time the viewfinder is updated, recompute layout
        preview.setOnPreviewOutputUpdateListener {

            // To update the SurfaceTexture, we have to remove it and re-add it
            val parent = viewFinder.parent as ViewGroup
            parent.removeView(viewFinder)
            parent.addView(viewFinder, 0)

            viewFinder.surfaceTexture = it.surfaceTexture
            updateTransform()
        }

        val imageCaptureConfig = ImageCaptureConfig.Builder()
            .apply {
                // We don't set a resolution for image capture; instead, we
                // select a capture mode which will infer the appropriate
                // resolution based on aspect ration and requested mode
                setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
            }.build()

        // Build the image capture use case and attach button click listener
        val imageCapture = ImageCapture(imageCaptureConfig)
        findViewById<ImageButton>(R.id.capture_button).setOnClickListener {
            val file = File(externalMediaDirs.first(),
                "${System.currentTimeMillis()}.jpg")

            imageCapture.takePicture(file, executor,
                object : ImageCapture.OnImageSavedListener {
                    override fun onError(
                        imageCaptureError: ImageCapture.ImageCaptureError,
                        message: String,
                        exc: Throwable?
                    ) {
                        val msg = "Photo capture failed: $message"
                        Log.e("CameraXApp", msg, exc)
                        viewFinder.post {
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onImageSaved(file: File) {
                        val msg = "Photo capture succeeded: ${file.absolutePath}"
                        setText(file)
                        Log.d("CameraXApp", msg)
                        viewFinder.post {
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        }


        CameraX.bindToLifecycle(
            this, preview, imageCapture)
    }

    private fun updateTransform() {
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = viewFinder.width / 2f
        val centerY = viewFinder.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when(viewFinder.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

        // Finally, apply transformations to our TextureView
        viewFinder.setTransform(matrix)
    }

    /**
     * Process result from permission request dialog box, has the request
     * been granted? If yes, start Camera. Otherwise display a toast
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                viewFinder.post { startCamera() }
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    private fun compressBitmap(bitmap:Bitmap, quality:Int):Bitmap{
        // Initialize a new ByteArrayStream
        val stream = ByteArrayOutputStream()

        /*
            **** reference source developer.android.com ***

            public boolean compress (Bitmap.CompressFormat format, int quality, OutputStream stream)
                Write a compressed version of the bitmap to the specified outputstream.
                If this returns true, the bitmap can be reconstructed by passing a
                corresponding inputstream to BitmapFactory.decodeStream().

                Note: not all Formats support all bitmap configs directly, so it is possible
                that the returned bitmap from BitmapFactory could be in a different bitdepth,
                and/or may have lost per-pixel alpha (e.g. JPEG only supports opaque pixels).

                Parameters
                format : The format of the compressed image
                quality : Hint to the compressor, 0-100. 0 meaning compress for small size,
                    100 meaning compress for max quality. Some formats,
                    like PNG which is lossless, will ignore the quality setting
                stream: The outputstream to write the compressed data.

                Returns
                    true if successfully compressed to the specified stream.


            Bitmap.CompressFormat
                Specifies the known formats a bitmap can be compressed into.

                    Bitmap.CompressFormat  JPEG
                    Bitmap.CompressFormat  PNG
                    Bitmap.CompressFormat  WEBP
        */

        // Compress the bitmap with JPEG format and quality 50%
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)

        val byteArray = stream.toByteArray()

        // Finally, return the compressed bitmap
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    @Suppress("DEPRECATION")
    fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri{
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        Log.e("Path",path.toString())
        return Uri.parse(path.toString())
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
}
