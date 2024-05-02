package com.example.myapplication.util

import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app

class DBUtil {

    companion object {
        var db = Firebase.firestore
        fun myFun(): FirebaseFirestore {
            return db
        }
    }
}