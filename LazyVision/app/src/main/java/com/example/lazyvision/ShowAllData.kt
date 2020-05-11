package com.example.lazyvision

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.google.android.gms.vision.text.Text
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_text_vision.*
import java.util.*
import kotlin.collections.ArrayList

class ShowAllData : AppCompatActivity(), Observer{

    private lateinit var vision_text: TextView
    private lateinit var home: Button
    private lateinit var again: Button
    private lateinit var text: Button
    private lateinit var showImage: Button
    private lateinit var resultText: String
    private lateinit var caseslist: MutableList<TextToFireBaseBeanClass>
    private lateinit var listView: ListView

    private var mdataAdapter: DataAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_all_data)
        Item
        Item.addObserver(this)

        val datalist: ListView= findViewById(R.id.listview)
        val myUriString = intent.getStringExtra("imageUri")
        val cno = intent.getStringExtra("caseno")
        val imgUrl = intent.getStringExtra("imageUrl")

        Log.e("CaseNo.", cno);

        home = findViewById(R.id.home)
        again = findViewById(R.id.again)
        text = findViewById(R.id.text)
        showImage = findViewById(R.id.showImage)
        mycaseno.text = cno.toString()

        listView=findViewById(R.id.listview)
        val data: ArrayList<TextToFireBaseBeanClass> = ArrayList()
        mdataAdapter= DataAdapter(this,R.layout.activity_show_all_data,data)

        datalist.adapter = mdataAdapter


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


        caseslist= mutableListOf()
        val path="caseno/c"+cno.substring(0,3)+"/evidence"

        Log.e("path",path)
        val refrence = FirebaseDatabase.getInstance().getReference(path)


        refrence.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }


            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {
                    caseslist.clear()
                    for(h in p0.children)
                    {
                        val data=h.getValue(TextToFireBaseBeanClass::class.java)
                        caseslist.add(data!!)

                    }

                }


            }

        })

    }

    override fun onStart() {
        super.onStart()
    }

    override fun update(o: Observable?, arg: Any?) {
        mdataAdapter?.clear()

        val data=Item.getData()
        if(data!=null)
        {
            mdataAdapter?.clear()
            mdataAdapter?.addAll()
            mdataAdapter?.notifyDataSetChanged()

        }
    }

    override fun onResume() {
        super.onResume()
        Item.addObserver(this)
    }

    override fun onPause() {
        super.onPause()
        Item.deleteObserver(this)
    }
}