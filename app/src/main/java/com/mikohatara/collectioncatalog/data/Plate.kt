package com.mikohatara.collectioncatalog.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import com.mikohatara.collectioncatalog.data.sample.Sample
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "plates", primaryKeys = ["number", "variant"])
data class Plate(
    @ColumnInfo(name = "country") var country: String,
    @ColumnInfo(name = "region") var region: String?,
    @ColumnInfo(name = "area") var area: String?,
    @ColumnInfo(name = "number") var number: String,
    @ColumnInfo(name = "type") var type: String,
    @ColumnInfo(name = "year") var year: Int?,
    @ColumnInfo(name = "period") var period: String?,
    @ColumnInfo(name = "vehicle") var vehicle: String?,
    @ColumnInfo(name = "status") var status: String,
    @ColumnInfo(name = "date") var date: String?,
    /*
    @ColumnInfo(name = "source_name") var sourceName: String?,
    @ColumnInfo(name = "source_type") var sourceType: String?,
    @ColumnInfo(name = "source_country") var sourceCountry: String?,
     */
    @Embedded var source: Source?,

    @ColumnInfo(name = "cost") var cost: Double?,
    @ColumnInfo(name = "value") var value: Double?,
    @ColumnInfo(name = "notes") var notes: String?,

    @ColumnInfo(name = "keeper") var keeper: Boolean,
    @ColumnInfo(name = "for_trade") var forTrade: Boolean,
    @ColumnInfo(name = "variant") var variant: Char = 'a',
    /*
    @ColumnInfo(name = "recipient") var recipient: String?,
    @ColumnInfo(name = "end_date") var endDate: String?,
    @ColumnInfo(name = "destination_country") var destinationCountry: String?,
    @ColumnInfo(name = "price") var price: Double?,
    */
    @Embedded var former: Former?
)

data class Source(
    @ColumnInfo(name = "source_name") var sourceName: String?,
    @ColumnInfo(name = "source_type") var sourceType: String?,
    @ColumnInfo(name = "source_country") var sourceCountry: String?
)

data class Former(
    @ColumnInfo(name = "recipient") var recipient: String?,
    @ColumnInfo(name = "end_date") var endDate: String?,
    @ColumnInfo(name = "destination_country") var destinationCountry: String?,
    @ColumnInfo(name = "price") var price: Double?
)

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