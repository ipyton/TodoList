package com.example.myapplication

import android.app.Application
import android.content.Context

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
        database = TodoItemDatabase.getDatabase(this)
    }

    companion object {
        lateinit var context: Application
        lateinit var database: TodoItemDatabase
    }
}