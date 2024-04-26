package com.mikohatara.collectioncatalog.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "plates", primaryKeys = ["number", "variant"])
data class Plate(
    @Embedded var commonDetails: CommonDetails,
    @Embedded var uniqueDetails: UniqueDetails,

)

@Entity(tableName = "wishlist")
data class WantedPlate(
    @PrimaryKey @Ignore val id: Int,
    @ColumnInfo(name = "country") var country: String,
    @ColumnInfo(name = "region") var region: String?,
    @ColumnInfo(name = "area") var area: String?,
    @ColumnInfo(name = "number") var number: String,
    @ColumnInfo(name = "type") var type: String,
    @ColumnInfo(name = "year") var year: Int?,
    @ColumnInfo(name = "period") var period: String?,
)

@Entity(tableName = "former", primaryKeys = ["number", "variant"])
data class FormerPlate(
    @Embedded var former: Former?
)

data class CommonDetails(
    @ColumnInfo(name = "country") var country: String,
    @ColumnInfo(name = "region") var region: String?,
    @ColumnInfo(name = "area") var area: String?,
    @ColumnInfo(name = "type") var type: String,
    @ColumnInfo(name = "period") var period: String?,
    @ColumnInfo(name = "year") var year: Int?,
)

data class UniqueDetails(
    @ColumnInfo(name = "number") var number: String,
    @ColumnInfo(name = "variant") var variant: Char = 'a',
    @ColumnInfo(name = "vehicle") var vehicle: String?,
    @ColumnInfo(name = "status") var status: String,
    @ColumnInfo(name = "date") var date: String?,
    @ColumnInfo(name = "cost") var cost: Double?,
    @ColumnInfo(name = "value") var value: Double?,
    @ColumnInfo(name = "notes") var notes: String?,
)

data class Booleans(
    @ColumnInfo(name = "keeper") var keeper: Boolean,
    @ColumnInfo(name = "for_trade") var forTrade: Boolean,
)

data class Source(
    @ColumnInfo(name = "source_name") var sourceName: String?,
    @ColumnInfo(name = "source_alias") var sourceAlias: String?,
    @ColumnInfo(name = "source_details") var sourceDetails: String?,
    @ColumnInfo(name = "source_type") var sourceType: String?,
    @ColumnInfo(name = "source_country") var sourceCountry: String?
)

data class Former(
    @ColumnInfo(name = "recipient") var recipient: String?,
    @ColumnInfo(name = "end_date") var endDate: String?,
    @ColumnInfo(name = "destination_country") var destinationCountry: String?,
    @ColumnInfo(name = "price") var price: Double?
)

