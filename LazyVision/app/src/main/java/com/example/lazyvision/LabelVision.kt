package com.example.lazyvision

import android.content.Intent
import android.graphics.Bitmap
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions
import kotlinx.android.synthetic.main.activity_label_vision.*
import kotlinx.android.synthetic.main.activity_text_vision.*
import kotlinx.android.synthetic.main.activity_text_vision.imageView
import kotlinx.android.synthetic.main.activity_text_vision.mycaseno

class LabelVision : AppCompatActivity() {
    private lateinit var vision_text: TextView
    private lateinit var home: Button
    private lateinit var again: Button
    private lateinit var text: Button
    private lateinit var showImage: Button
    private lateinit var originalCameraImage: Bitmap



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_label_vision)


        val myUriString = intent.getStringExtra("imageUri")
        val cno = intent.getStringExtra("caseno")


        vision_text = findViewById(R.id.vision_text)
        home = findViewById(R.id.home)
        again = findViewById(R.id.again)
        text = findViewById(R.id.text)
        showImage = findViewById(R.id.showImage)
        mycaseno.text = cno.toString()

        text.setOnClickListener({
            imageView.isVisible = false
            vision_text.isVisible = true
        })

        home.setOnClickListener({
            intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
            finish()
        })

        again.setOnClickListener({
            intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
            finish()
        })

        showImage.setOnClickListener({
            imageView.isVisible = true
            vision_text.isVisible = false
            imageView.setImageURI(myUriString.toUri())
        })

        val uri = myUriString.toUri()
        val image = FirebaseVisionImage.fromFilePath(this, uri)

        val labeler = FirebaseVision.getInstance().getCloudImageLabeler()
        val base = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        val originalCameraImage: Bitmap = base.copy(Bitmap.Config.ARGB_8888, true)

        labeler.processImage(image)
            .addOnSuccessListener { labels ->
                var st : String
                st = ""
                for (label in labels) {
                    val text = label.text
                    val entityId = label.entityId
                    val confidence = label.confidence
                    st+=text
                    st+=": "
                    st+=confidence*100
                    st+="%\n"
                }
                vision_text.setText(st)
            }
            .addOnFailureListener { e ->
                vision_text.setText(e.toString())
            }
    }

    override fun onStart() {
        super.onStart()
        imageView.isVisible = false
    }
}
