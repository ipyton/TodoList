package com.example.myapplication.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TodoItem(
    var documentId: String,
    var date: String,
    var introduction: String,
    var isDone: Boolean,
    var latitude: Double,
    var longitude: Double,
    var time: String,
    var title: String,
    var selected : Boolean,
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0
){

    constructor() : this("", "", "", false, 0.0, 0.0, "", "", false)

}