package com.example.myapplication.Pages

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import com.example.myapplication.Activities.ActivityItem

@Composable
fun Scheduled() {
    val productList = listOf(
        1,2,3
        // Add more items as needed
    )
    LazyColumn {
        itemsIndexed(productList) {idx, count ->
            ActivityItem()
        }
    }
}