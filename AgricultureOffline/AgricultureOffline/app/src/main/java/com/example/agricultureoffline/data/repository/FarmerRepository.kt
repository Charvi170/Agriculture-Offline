package com.example.agricultureoffline.data.repository

import com.example.agricultureoffline.data.local.Farmer
import com.example.agricultureoffline.data.local.FarmerDao

class FarmerRepository( private val farmerDao: FarmerDao)
{
    suspend fun insertFarmer(farmer: Farmer): Long {
        return farmerDao.insertFarmer(farmer)
    }

    suspend fun updateFarmer(farmer: Farmer) {
        farmerDao.updateFarmer(farmer)
    }

    suspend fun getFarmerByPhone(phone: String): Farmer? {
        return farmerDao.getFarmerByPhone(phone)
    }
}