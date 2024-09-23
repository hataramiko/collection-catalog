package com.mikohatara.collectioncatalog.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlateDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlate(plate: Plate)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlateCollectionCrossRef(crossRef: PlateCollectionCrossRef)

    @Update
    suspend fun updatePlate(plate: Plate)

    @Delete
    suspend fun deletePlate(plate: Plate)

    @Query("DELETE from plate_collection_cross_ref WHERE plate_id = :plateId")
    suspend fun deletePlateCollectionCrossRefs(plateId: Int)

    @Query("SELECT * from plates ORDER BY country ASC")
    fun getAllPlates(): Flow<List<Plate>>

    @Query("SELECT * from plates WHERE id = :id")
    fun getPlate(id: Int): Flow<Plate>

    @Transaction
    @Query("SELECT * from plates WHERE id = :id")
    fun getPlateWithCollections(id: Int): Flow<PlateWithCollections>

    /*@Transaction
    suspend fun insertPlateWithCollections(plate: Plate, collectionIds: List<Int>) {
        val plateId = insertPlate(plate)
        collectionIds.forEach { collectionId ->
            insertPlateCollectionCrossRef(PlateCollectionCrossRef(plateId, collectionId))
        }
    }*/

    @Transaction
    suspend fun updatePlateWithCollections(plate: Plate, collectionIds: List<Int>) {
        updatePlate(plate)
        deletePlateCollectionCrossRefs(plate.id)
        collectionIds.forEach { collectionId ->
            insertPlateCollectionCrossRef(PlateCollectionCrossRef(plate.id, collectionId))
        }
    }

    //

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

    //

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
