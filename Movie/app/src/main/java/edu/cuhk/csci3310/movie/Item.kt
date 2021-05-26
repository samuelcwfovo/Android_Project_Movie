package edu.cuhk.csci3310.movie

data class Item (
    val hot : Int,
    val title :String,
    val postViewSrc :String,
    val label :String,
    val minute : String,
    val rate : Float,
    val detail : String,
    val photoList : List<String>,
    val youtubeLink : String
)