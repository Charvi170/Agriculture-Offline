package com.example.agricultureoffline.data.repository

import com.example.agricultureoffline.data.local.Scan
import com.example.agricultureoffline.data.local.ScanDao

class ScanRepository(
    private val scanDao: ScanDao
) {

    suspend fun insertScan(
        scan: Scan
    ): Long {
        return scanDao.insertScan(scan)
    }

    suspend fun getRecentScans(
        farmerId: Int,
        limit: Int = 5
    ): List<Scan> {
        val list = if (farmerId > 0) scanDao.getRecentScans(farmerId, limit) else emptyList()
        return if (list.isNotEmpty()) list else scanDao.getRecentScans(limit)
    }

    suspend fun getAllScans(
        farmerId: Int
    ): List<Scan> {
        val list = if (farmerId > 0) scanDao.getScansForFarmer(farmerId) else emptyList()
        return if (list.isNotEmpty()) list else scanDao.getAllScans()
    }

    suspend fun getAllScans(): List<Scan> {
        return scanDao.getAllScans()
    }

    suspend fun getScanById(
        scanId: Int
    ): Scan? {
        return scanDao.getScanById(scanId)
    }

    suspend fun deleteScan(
        scanId: Int
    ): Boolean {
        return scanDao.deleteScanById(scanId) > 0
    }
}