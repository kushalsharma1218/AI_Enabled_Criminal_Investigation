package com.example.lazyvision

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Evidence(
    var result: String = "",
    var imgurl: String = "",
    var label: String = ""

)