package com.example.lazyvision

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import kotlinx.android.synthetic.main.activity_progress_bar.*

class ProgressBar : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress_bar)

        progress_circular.apply {
            progressMax =100f
            setProgressWithAnimation(50f ,80000)

            progressBarWidth=5f
            backgroundProgressBarWidth=7f
            progressBarColor= Color.GREEN
            roundBorder = true
            startAngle = 180f
            progressDirection = CircularProgressBar.ProgressDirection.TO_RIGHT

        }
        progress_circular.onProgressChangeListener = { progress ->
            // Do something

        }


    }
}
