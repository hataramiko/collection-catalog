package com.mikohatara.collectioncatalog.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "plates", primaryKeys = ["number", "variant"])
data class Plate(
    @Embedded val commonDetails: CommonDetails,
    @Embedded val uniqueDetails: UniqueDetails,
    @Embedded val grading: Grading,
    @Embedded val source: Source,
    @Embedded val measurements: Measurements
)

@Entity(tableName = "wishlist")
data class WantedPlate(
    @PrimaryKey val id: Int,
    @Embedded val commonDetails: CommonDetails,
    @ColumnInfo(name = "number") val number: String?,
    @ColumnInfo(name = "notes") val notes: String?
)

@Entity(tableName = "former", primaryKeys = ["number", "variant"])
data class FormerPlate(
    @Embedded val commonDetails: CommonDetails,
    @Embedded val uniqueDetails: UniqueDetails,
    @Embedded val source: Source,
    @Embedded val former: Former?,
    @Embedded val measurements: Measurements
)

data class CommonDetails(
    @ColumnInfo(name = "country") val country: String,
    @ColumnInfo(name = "region") val region: String?,
    @ColumnInfo(name = "area") val area: String?,
    /*@ColumnInfo(name = "region_1st") var region1st: String?,
    @ColumnInfo(name = "region_2nd") var region2nd: String?,
    @ColumnInfo(name = "region_3rd") var region3rd: String?,*/
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "period") val period: String?,
    /*@ColumnInfo(name = "period_start") var periodStart: Int?,
    @ColumnInfo(name = "period_end") var periodEnd: Int?,*/
    @ColumnInfo(name = "year") val year: Int?
)

data class UniqueDetails(
    @ColumnInfo(name = "number") val number: String,
    @ColumnInfo(name = "variant") val variant: String,
    @ColumnInfo(name = "image_path") val imagePath: String?,
    @ColumnInfo(name = "vehicle") val vehicle: String?,
    @ColumnInfo(name = "notes") val notes: String?,
    @ColumnInfo(name = "date") val date: String?,
    @ColumnInfo(name = "cost") val cost: Long?,
    @ColumnInfo(name = "value") val value: Long?,
    @ColumnInfo(name = "status") val status: String?,
)

data class Grading(
    @ColumnInfo(name = "keeper") val isKeeper: Boolean,
    @ColumnInfo(name = "for_trade") val isForTrade: Boolean,
    @ColumnInfo(name = "condition") val condition: String?,
)

data class Measurements(
    @ColumnInfo(name = "width") val width: Int?,
    @ColumnInfo(name = "height") val height: Int?,
    @ColumnInfo(name = "weight") val weight: Double?
)

data class Source(
    @ColumnInfo(name = "source_name") val sourceName: String?,
    @ColumnInfo(name = "source_alias") val sourceAlias: String?,
    @ColumnInfo(name = "source_details") val sourceDetails: String?,
    @ColumnInfo(name = "source_type") val sourceType: String?,
    @ColumnInfo(name = "source_country") val sourceCountry: String?
)

data class Former(
    @ColumnInfo(name = "recipient") val recipient: String?,
    @ColumnInfo(name = "transaction_date") val endDate: String?,
    @ColumnInfo(name = "transaction_details") val transactionDetails: String?,
    @ColumnInfo(name = "price") val price: Double?,
    @ColumnInfo(name = "transaction_type") val transactionType: String?,
    @ColumnInfo(name = "destination_country") val destinationCountry: String?,
)
