package com.mikohatara.collectioncatalog.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plates")
data class Plate(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @Embedded val commonDetails: CommonDetails,
    @Embedded val uniqueDetails: UniqueDetails,
    @Embedded val grading: Grading,
    @Embedded val size: Size,
    @Embedded val color: Color,
    @Embedded val source: Source
)

@Entity(tableName = "wishlist")
data class WantedPlate(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "reg_no") val regNo: String?,
    @Embedded val commonDetails: CommonDetails,
    @ColumnInfo(name = "notes") val notes: String?
)

@Entity(tableName = "archive")
data class FormerPlate(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @Embedded val commonDetails: CommonDetails,
    @Embedded val uniqueDetails: UniqueDetails,
    @Embedded val size: Size,
    @Embedded val color: Color,
    @Embedded val source: Source,
    @Embedded val archivalDetails: ArchivalDetails,
)

data class ItemDetails(
    val id: Int? = null,
    // CommonDetails
    val country: String? = null,
    val region1st: String? = null,
    val region2nd: String? = null,
    val region3rd: String? = null,
    val type: String? = null,
    val periodStart: Int? = null,
    val periodEnd: Int? = null,
    val year: Int? = null,
    // UniqueDetails
    val regNo: String? = null,
    val imagePath: String? = null,
    val notes: String? = null,
    val vehicle: String? = null,
    val date: String? = null,
    val cost: Long? = null,
    val value: Long? = null,
    val status: String? = null,
    // Grading
    val isKeeper: Boolean = false,
    val isForTrade: Boolean = false,
    // Size
    val width: Int? = null,
    val height: Int? = null,
    val weight: Double? = null,
    // Color
    val colorMain: String? = null,
    val colorSecondary: String? = null,
    // Source
    val sourceName: String? = null,
    val sourceAlias: String? = null,
    val sourceDetails: String? = null,
    val sourceType: String? = null,
    val sourceCountry: String? = null,
    // ArchivalDetails
    val archivalDate: String? = null,
    val recipientName: String? = null,
    val recipientAlias: String? = null,
    val actionType: String? = null,
    val actionDetails: String? = null,
    val price: Long? = null,
    val recipientCountry: String? = null
)

data class CommonDetails(
    @ColumnInfo(name = "country") val country: String,
    @ColumnInfo(name = "region_1st") val region1st: String?,
    @ColumnInfo(name = "region_2nd") val region2nd: String?,
    @ColumnInfo(name = "region_3rd") val region3rd: String?,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "period_start") val periodStart: Int?,
    @ColumnInfo(name = "period_end") val periodEnd: Int?,
    @ColumnInfo(name = "year") val year: Int?
)

data class UniqueDetails( // TODO include "condition" and/or "grade"???
    @ColumnInfo(name = "reg_no") val regNo: String,
    @ColumnInfo(name = "image_path") val imagePath: String?,
    @ColumnInfo(name = "notes") val notes: String?,
    @ColumnInfo(name = "vehicle") val vehicle: String?,
    @ColumnInfo(name = "date") val date: String?,
    @ColumnInfo(name = "cost") val cost: Long?,
    @ColumnInfo(name = "value") val value: Long?,
    @ColumnInfo(name = "status") val status: String?
)

data class Grading( // TODO get rid of when "runs" have been implemented
    @ColumnInfo(name = "keeper") val isKeeper: Boolean,
    @ColumnInfo(name = "for_trade") val isForTrade: Boolean,
)

data class Size(
    @ColumnInfo(name = "width") val width: Int?,
    @ColumnInfo(name = "height") val height: Int?,
    @ColumnInfo(name = "weight") val weight: Double?
)

data class Color(
    @ColumnInfo(name = "color_main") val main: String?,
    @ColumnInfo(name = "color_secondary") val secondary: String?
)

data class Source(
    @ColumnInfo(name = "source_name") val name: String?,
    @ColumnInfo(name = "source_alias") val alias: String?,
    @ColumnInfo(name = "source_type") val type: String?,
    @ColumnInfo(name = "source_details") val details: String?,
    @ColumnInfo(name = "source_country") val country: String?
)

data class ArchivalDetails(
    @ColumnInfo(name = "archival_date") val date: String?,
    @ColumnInfo(name = "recipient_name") val recipientName: String?,
    @ColumnInfo(name = "recipient_alias") val recipientAlias: String?,
    @ColumnInfo(name = "action_type") val actionType: String?,
    @ColumnInfo(name = "action_details") val actionDetails: String?,
    @ColumnInfo(name = "price") val price: Long?,
    @ColumnInfo(name = "recipient_country") val recipientCountry: String?,
)
