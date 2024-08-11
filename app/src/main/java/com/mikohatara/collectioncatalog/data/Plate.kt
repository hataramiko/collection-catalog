package com.mikohatara.collectioncatalog.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "plates", primaryKeys = ["number", "variant"])
data class Plate(
    @Embedded var commonDetails: CommonDetails,
    @Embedded var uniqueDetails: UniqueDetails,
    @Embedded var grading: Grading,
    @Embedded var source: Source,
    @Embedded var measurements: Measurements
)

@Entity(tableName = "wishlist")
data class WantedPlate(
    @PrimaryKey val id: Int,
    @Embedded var commonDetails: CommonDetails,
    @ColumnInfo(name = "number") var number: String?,
    @ColumnInfo(name = "notes") var notes: String?
)

@Entity(tableName = "former", primaryKeys = ["number", "variant"])
data class FormerPlate(
    @Embedded var commonDetails: CommonDetails,
    @Embedded var uniqueDetails: UniqueDetails,
    @Embedded var source: Source,
    @Embedded var former: Former?,
    @Embedded var measurements: Measurements
)

data class CommonDetails(
    @ColumnInfo(name = "country") var country: String,
    @ColumnInfo(name = "region") var region: String?,
    @ColumnInfo(name = "area") var area: String?,
    @ColumnInfo(name = "type") var type: String,
    @ColumnInfo(name = "period") var period: String?,
    @ColumnInfo(name = "year") var year: Int?
)

data class UniqueDetails(
    @ColumnInfo(name = "number") var number: String,
    @ColumnInfo(name = "variant") var variant: String,
    @ColumnInfo(name = "image_path") var imagePath: String?,
    @ColumnInfo(name = "vehicle") var vehicle: String?,
    @ColumnInfo(name = "notes") var notes: String?,
    @ColumnInfo(name = "date") var date: String?,
    @ColumnInfo(name = "cost") var cost: Double?,
    @ColumnInfo(name = "value") var value: Double?,
    @ColumnInfo(name = "status") var status: String?,
)

data class Grading(
    @ColumnInfo(name = "keeper") var isKeeper: Boolean,
    @ColumnInfo(name = "for_trade") var isForTrade: Boolean,
    @ColumnInfo(name = "condition") var condition: String?,
)

data class Measurements(
    @ColumnInfo(name = "width") var width: Double?,
    @ColumnInfo(name = "height") var height: Double?,
    @ColumnInfo(name = "weight") var weight: Double?
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
    @ColumnInfo(name = "transaction_date") var endDate: String?,
    @ColumnInfo(name = "transaction_details") var transactionDetails: String?,
    @ColumnInfo(name = "price") var price: Double?,
    @ColumnInfo(name = "transaction_type") var transactionType: String?,
    @ColumnInfo(name = "destination_country") var destinationCountry: String?,
)
