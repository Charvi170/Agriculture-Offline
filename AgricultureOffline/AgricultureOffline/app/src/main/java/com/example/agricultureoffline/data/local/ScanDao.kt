package com.example.agricultureoffline.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ScanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: Scan): Long

    @Query("SELECT * FROM scan_history ORDER BY date_time DESC")
    suspend fun getAllScans(): List<Scan>

    @Query("""
        SELECT * FROM scan_history
        WHERE farmer_id = :farmerId
        ORDER BY date_time DESC
    """)
    suspend fun getScansForFarmer(farmerId: Int): List<Scan>

    @Query("""
        SELECT * FROM scan_history
        WHERE farmer_id = :farmerId
        ORDER BY date_time DESC
        LIMIT :limit
    """)
    suspend fun getRecentScans(farmerId: Int, limit: Int = 5): List<Scan>

    @Query("SELECT * FROM scan_history ORDER BY date_time DESC LIMIT :limit")
    suspend fun getRecentScans(limit: Int = 5): List<Scan>

    @Query("SELECT * FROM scan_history WHERE id = :scanId LIMIT 1")
    suspend fun getScanById(scanId: Int): Scan?

    @Query("DELETE FROM scan_history WHERE id = :scanId")
    suspend fun deleteScanById(scanId: Int): Int

    @Delete
    suspend fun deleteScan(scan: Scan)
}