package com.example.myapplication.entities

import com.google.firebase.database.PropertyName

data class TodoItem(
    var documentId: String,
    var date: String,
    var introduction: String,
    var isDone: Boolean,
    var latitude: Double,
    var longitude: Double,
    var time: String,
    var title: String,
    var selected : Boolean
){
    constructor() : this("","", "", false, 0.0, 0.0, "", "", false)

}