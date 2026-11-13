package com.example.agricultureoffline.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "diseases",
    indices = [Index(value = ["class_name"], unique = true)]
)
data class Disease(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "class_name")
    val className: String,

    @ColumnInfo(name = "plant_name")
    val plantName: String = "",

    @ColumnInfo(name = "disease_name")
    val diseaseName: String = "",

    @ColumnInfo(name = "symptoms")
    val symptoms: String = "",

    @ColumnInfo(name = "treatment")
    val treatment: String = "",

    @ColumnInfo(name = "prevention")
    val prevention: String = "",

    @ColumnInfo(name = "source_url")
    val sourceUrl: String = ""
) {
    // Backwards-compatible aliases for existing code
    val diseaseId: Int get() = id
    val name: String get() = if (diseaseName.isNotBlank()) diseaseName else className
}