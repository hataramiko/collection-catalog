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

    @Query("SELECT * from plates WHERE number = :number AND variant = :variant") //
    fun getPlate(number: String, variant: String): Flow<Plate>

    @Query("SELECT * from plates WHERE number = :number")
    fun getPlates(number: String): Flow<Plate>

    @Query("SELECT * from plates ORDER BY country ASC")
    fun getAllPlates(): Flow<List<Plate>>

    @Query("SELECT * from plates WHERE number = :number AND variant = :variant")
    fun getPlateData(number: String, variant: String): Plate
}