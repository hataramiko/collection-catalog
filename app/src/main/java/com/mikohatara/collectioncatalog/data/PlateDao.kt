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
    suspend fun insert(plate: Plate)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(vararg plates: Plate)

    @Update
    suspend fun update(plate: Plate)

    @Update
    suspend fun updateAll(vararg plates: Plate)

    @Delete
    suspend fun delete(plate: Plate)

    @Query("SELECT * from plates WHERE number = :number")
    fun getPlate(number: String): Flow<Plate>

    @Query("SELECT * from plates ORDER BY country ASC")
    fun getAllPlates(): Flow<List<Plate>>
}