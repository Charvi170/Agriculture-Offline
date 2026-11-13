package com.example.agricultureoffline.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FarmerDao {
    @Insert
    suspend fun insertFarmer(farmer: Farmer): Long

    @androidx.room.Update
    suspend fun updateFarmer(farmer: Farmer)

    @Query("SELECT * FROM Farmer WHERE phoneNo = :phone")
    suspend fun getFarmerByPhone(phone: String): Farmer?
}