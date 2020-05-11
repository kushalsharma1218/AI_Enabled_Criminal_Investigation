package com.example.lazyvision

import com.google.firebase.database.DataSnapshot

class TextToFireBaseBeanClass(snapshot: DataSnapshot, st: String){
    lateinit var imageUrl:String
    lateinit var textgenerated:String


    init {
        try {
            val data:HashMap<String,Any> = snapshot as HashMap<String,Any>
            imageUrl = data["image"] as String
            textgenerated = data["label"] as String
        } catch ( e:Exception)
        {
            e.printStackTrace()
        }
    }
}