package com.mikohatara.collectioncatalog.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlateDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlate(plate: Plate)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAllPlates(vararg plates: Plate)

    @Update
    suspend fun updatePlate(plate: Plate)

    @Update
    suspend fun updateAllPlates(vararg plates: Plate)

    @Delete
    suspend fun deletePlate(plate: Plate)

    @Query("SELECT * from plates WHERE id = :id")
    fun getPlate(id: Int): Flow<Plate>

    @Query("SELECT * from plates WHERE reg_no = :regNo")
    fun getPlates(regNo: String): Flow<Plate>

    @Query("SELECT * from plates ORDER BY country ASC")
    fun getAllPlates(): Flow<List<Plate>>

    @Query("SELECT * from plates WHERE id = :id")
    fun getPlateData(id: Int): Plate
}