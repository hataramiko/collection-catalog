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

    @Update
    suspend fun updatePlate(plate: Plate)

    @Delete
    suspend fun deletePlate(plate: Plate)

    @Query("SELECT * from plates ORDER BY country ASC")
    fun getAllPlates(): Flow<List<Plate>>

    @Query("SELECT * from plates WHERE id = :id")
    fun getPlate(id: Int): Flow<Plate>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWantedPlate(wantedPlate: WantedPlate)

    @Update
    suspend fun updateWantedPlate(wantedPlate: WantedPlate)

    @Delete
    suspend fun deleteWantedPlate(wantedPlate: WantedPlate)

    @Query("SELECT * from wishlist ORDER BY country ASC")
    fun getAllWantedPlates(): Flow<List<WantedPlate>>

    @Query("SELECT * from wishlist WHERE id = :id")
    fun getWantedPlate(id: Int): Flow<WantedPlate>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFormerPlate(formerPlate: FormerPlate)

    @Update
    suspend fun updateFormerPlate(formerPlate: FormerPlate)

    @Delete
    suspend fun deleteFormerPlate(formerPlate: FormerPlate)

    @Query("SELECT * from archive ORDER BY country ASC")
    fun getAllFormerPlates(): Flow<List<FormerPlate>>

    @Query("SELECT * from archive WHERE id = :id")
    fun getFormerPlate(id: Int): Flow<FormerPlate>
}
