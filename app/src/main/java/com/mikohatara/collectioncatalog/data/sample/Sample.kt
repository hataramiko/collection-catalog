package com.mikohatara.collectioncatalog.data.sample

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "samples")
data class Sample(
    val country: String,
    val number: String,
    val type: String,
    @PrimaryKey val id: String
)

@Dao
interface SampleDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(sample: Sample)

    @Update
    suspend fun update(sample: Sample)

    @Delete
    suspend fun delete(sample: Sample)

    @Query("SELECT * from samples WHERE id = :id")
    fun getSample(id: String): Flow<Sample>

    @Query("SELECT * from samples ORDER BY country ASC")
    fun getAllSamples(): Flow<List<Sample>>
}
