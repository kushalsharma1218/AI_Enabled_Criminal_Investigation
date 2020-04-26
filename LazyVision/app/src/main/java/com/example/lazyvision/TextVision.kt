package com.example.lazyvision

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions
import kotlinx.android.synthetic.main.activity_text_vision.*


class TextVision : AppCompatActivity() {

    private lateinit var vision_text: TextView
    private lateinit var home: Button
    private lateinit var again: Button
    private lateinit var text: Button
    private lateinit var showImage: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_vision)


        val myUriString = intent.getStringExtra("imageUri")

        vision_text = findViewById(R.id.vision_text)
        home = findViewById(R.id.home)
        again = findViewById(R.id.again)
        text = findViewById(R.id.text)
        showImage = findViewById(R.id.showImage)

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
        val detector = FirebaseVision.getInstance().cloudTextRecognizer

        val options = FirebaseVisionCloudTextRecognizerOptions.Builder()
            .setLanguageHints(listOf("en", "hi"))
            .build()

        val result = detector.processImage(image)
            .addOnSuccessListener { firebaseVisionText ->
                val resultText = firebaseVisionText.text
                vision_text.setText(resultText)
            }
            .addOnFailureListener { e ->
                vision_text.setText("ERROR!!")
            }
    }

    override fun onStart() {
        super.onStart()
        imageView.isVisible = false
    }
}
