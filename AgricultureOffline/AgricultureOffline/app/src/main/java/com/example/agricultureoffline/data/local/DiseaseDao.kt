package com.example.agricultureoffline.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DiseaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDisease(disease: Disease): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(diseases: List<Disease>)

    @Query("SELECT * FROM diseases WHERE class_name = :className LIMIT 1")
    suspend fun getDiseaseByClassName(className: String): Disease?

    @Query("""
        SELECT * FROM diseases 
        WHERE class_name = :name 
           OR disease_name = :name 
        LIMIT 1
    """)
    suspend fun getDiseaseByName(name: String): Disease?

    @Query("SELECT * FROM diseases WHERE id = :id LIMIT 1")
    suspend fun getDiseaseById(id: Int): Disease?

    @Query("SELECT * FROM diseases ORDER BY plant_name ASC, disease_name ASC")
    suspend fun getAllDiseases(): List<Disease>

    @Query("SELECT COUNT(*) FROM diseases")
    suspend fun getDiseaseCount(): Int
}