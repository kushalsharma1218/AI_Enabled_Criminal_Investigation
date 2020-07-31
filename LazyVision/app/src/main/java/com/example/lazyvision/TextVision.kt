package com.example.lazyvision

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_text_vision.*
import java.io.ByteArrayOutputStream

class TextVision : AppCompatActivity() {

    private lateinit var vision_text: TextView
    private lateinit var home: Button
    private lateinit var again: Button
    private lateinit var text: Button
    private lateinit var showImage: Button
    private lateinit var database: DatabaseReference



    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_vision)


        val myUriString = intent.getStringExtra("imageUri")
        val cno = intent.getStringExtra("caseno")

        vision_text = findViewById(R.id.vision_text)
        home = findViewById(R.id.home)
        again = findViewById(R.id.again)
        text = findViewById(R.id.text)
        showImage = findViewById(R.id.showImage)
        caseno.text = cno.toString()

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
                val res = firebaseVisionText.text
                vision_text.setText(res)

                //compressing the image for upload
                val source = ImageDecoder.createSource(this.contentResolver, uri)
                val bitmap = ImageDecoder.decodeBitmap(source)
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos)
                val data = baos.toByteArray()

                //storing the byte array to firebase
                val storage = Firebase.storage
                var storageRef = storage.reference
                var imagesRef = storageRef.child("images/${uri.lastPathSegment}")
                var uploadTask = imagesRef.putBytes(data)
                uploadTask.addOnFailureListener {
                    Toast.makeText(baseContext,"Image upload on cloud failed", Toast.LENGTH_SHORT)
                }.addOnSuccessListener {
                    val imgurl = imagesRef.path
                    database = Firebase.database.reference
                    val evidence = Evidence(res, "gs://lazyvision-9cb06.appspot.com/images/${uri.lastPathSegment}", "TEXT VISION")
                    val key = database.child("caseno").child(cno.toString()).child("evidence").push().key
                    database.child("caseno").child(cno.toString()).child("evidence").child(key.toString()).setValue(evidence)
                    Toast.makeText(baseContext, "Evidence Successfully uploaded on cloud : ${imgurl}", Toast.LENGTH_LONG)
                }

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
