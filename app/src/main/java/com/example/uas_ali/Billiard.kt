package com.example.uas_ali

import com.google.firebase.database.Exclude

data class Billiard(
    var name:String? = null,
    var desc:String? = null,
    var imageUrl:String? = null,
    @get:Exclude
    @set:Exclude
    var key:String? = null
)