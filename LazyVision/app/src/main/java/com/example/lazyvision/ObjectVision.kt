package com.example.lazyvision

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.objects.FirebaseVisionObject
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions
import kotlinx.android.synthetic.main.activity_text_vision.*


class ObjectVision : AppCompatActivity() {

    private lateinit var vision_text: TextView
    private lateinit var home: Button
    private lateinit var again: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_object_vision)


        val myUriString = intent.getStringExtra("imageUri")

        home = findViewById(R.id.home)
        again = findViewById(R.id.again)


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


        val options = FirebaseVisionObjectDetectorOptions.Builder()
            .setDetectorMode(FirebaseVisionObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()  // Optional
            .build()


        val uri = myUriString.toUri()
        val image = FirebaseVisionImage.fromFilePath(this, uri)
        val objectDetector = FirebaseVision.getInstance().getOnDeviceObjectDetector(options)
        val base = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        val bitmap: Bitmap = base.copy(Bitmap.Config.ARGB_8888, true)


        imageView.setImageBitmap(bitmap)


        objectDetector.processImage(image)
            .addOnSuccessListener { detectedObjects ->

                val drawingView = DrawingView(applicationContext, detectedObjects)
                drawingView.draw(Canvas(bitmap))
                runOnUiThread {
                    imageView.setImageBitmap(bitmap)
                }
            }
            .addOnFailureListener { e ->
                vision_text.setText(e.toString())
            }

    }
}

class DrawingView(context: Context, var visionObjects: List<FirebaseVisionObject>) : View(context) {

    companion object {
        // mapping table for category to strings: drawing strings
        val categoryNames: Map<Int, String> = mapOf(
            FirebaseVisionObject.CATEGORY_UNKNOWN to "Unknown",
            FirebaseVisionObject.CATEGORY_HOME_GOOD to "Home Goods",
            FirebaseVisionObject.CATEGORY_FASHION_GOOD to "Fashion Goods",
            FirebaseVisionObject.CATEGORY_FOOD to "Food",
            FirebaseVisionObject.CATEGORY_PLACE to "Place",
            FirebaseVisionObject.CATEGORY_PLANT to "Plant"
        )
    }

    val MAX_FONT_SIZE = 96F

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val pen = Paint()
        pen.textAlign = Paint.Align.LEFT

        for (item in visionObjects) {
            // draw bounding box
            pen.color = Color.RED
            pen.strokeWidth = 8F
            pen.style = Paint.Style.STROKE
            val box = item.getBoundingBox()
            canvas.drawRect(box, pen)

            // Draw result category, and confidence
            val tags: MutableList<String> = mutableListOf()
            tags.add("Category: ${categoryNames[item.classificationCategory]}")
            if (item.classificationCategory != FirebaseVisionObject.CATEGORY_UNKNOWN) {
                tags.add("Confidence: ${item.classificationConfidence!!.times(100).toInt()}%")
            }

            var tagSize = Rect(0, 0, 0, 0)
            var maxLen = 0
            var index: Int = -1

            for ((idx, tag) in tags.withIndex()) {
                if (maxLen < tag.length) {
                    maxLen = tag.length
                    index = idx
                }
            }

            // calculate the right font size
            pen.style = Paint.Style.FILL_AND_STROKE
            pen.color = Color.YELLOW
            pen.strokeWidth = 2F

            pen.textSize = MAX_FONT_SIZE
            pen.getTextBounds(tags[index], 0, tags[index].length, tagSize)
            val fontSize: Float = pen.textSize * box.width() / tagSize.width()

            // adjust the font size so texts are inside the bounding box
            if (fontSize < pen.textSize) pen.textSize = fontSize

            var margin = (box.width() - tagSize.width()) / 2.0F
            if (margin < 0F) margin = 0F

            // draw tags onto bitmap (bmp is in upside down format)
            for ((idx, txt) in tags.withIndex()) {
                canvas.drawText(
                    txt, box.left + margin,
                    box.top + tagSize.height().times(idx + 1.0F), pen
                )
            }
        }
    }
}
