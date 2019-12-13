package com.chsltutorials.blognews.model

import com.google.firebase.database.ServerValue

data class Comment(
    var id : String = "",
    var name : String = "",
    var content : String = "",
    var image : String = "",
    var timestamp : Any = ServerValue.TIMESTAMP
)