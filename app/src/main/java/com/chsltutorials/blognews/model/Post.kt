package com.chsltutorials.blognews.model

import com.google.firebase.database.ServerValue

data class Post (
    var postKey : String? = "",
    var title : String? = "",
    var description : String? = "",
    var pictures : String? = "",
    var userId : String? = "",
    var userPhoto : String? = "",
    var timestamp : Any = ServerValue.TIMESTAMP)