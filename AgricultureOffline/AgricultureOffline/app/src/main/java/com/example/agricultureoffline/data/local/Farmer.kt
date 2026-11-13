package com.example.agricultureoffline.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Farmer(
    @PrimaryKey (autoGenerate = true)
    val farmerId:Int =0,
    val name: String,
    val phoneNo: String,
    val loginTimestamp: Long = System.currentTimeMillis()
)

