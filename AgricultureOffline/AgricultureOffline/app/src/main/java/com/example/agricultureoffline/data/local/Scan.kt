package com.example.agricultureoffline.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class Scan(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "farmer_id")
    val farmerId: Int = 0,

    @ColumnInfo(name = "image_path")
    val imagePath: String,

    @ColumnInfo(name = "predicted_class")
    val predictedClass: String = "",

    @ColumnInfo(name = "disease_name")
    val diseaseName: String = "",

    @ColumnInfo(name = "disease_id")
    val diseaseId: Int = 0,

    @ColumnInfo(name = "confidence")
    val confidence: Float,

    @ColumnInfo(name = "status")
    val status: String = "Completed",

    @ColumnInfo(name = "scanned_at")
    val scannedAt: String = "",

    @ColumnInfo(name = "date_time")
    val dateTime: Long = System.currentTimeMillis()
) {
    // Backwards-compatible alias for existing code
    val scanId: Int get() = id
}