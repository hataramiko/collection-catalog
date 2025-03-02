package com.mikohatara.collectioncatalog.util

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.mikohatara.collectioncatalog.data.Color
import com.mikohatara.collectioncatalog.data.CommonDetails
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.Size
import com.mikohatara.collectioncatalog.data.Source
import com.mikohatara.collectioncatalog.data.UniqueDetails
import com.opencsv.CSVReaderBuilder
import com.opencsv.CSVWriter
import com.opencsv.bean.CsvBindByName
import com.opencsv.bean.StatefulBeanToCsvBuilder
import java.io.BufferedReader
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.InputStreamReader

fun exportPlatesToCsv(context: Context, plates: List<Plate>, fileName: String) {
    val csvPlateList = plates.map { plate ->
        CsvPlate(
            //id = plate.id ?: 0, // ID shouldn't be exported?
            regNo = plate.uniqueDetails.regNo ?: "",
            // CommonDetails
            country = plate.commonDetails.country ?: "",
            region1st = plate.commonDetails.region1st,
            region2nd = plate.commonDetails.region2nd,
            region3rd = plate.commonDetails.region3rd,
            type = plate.commonDetails.type ?: "",
            periodStart = plate.commonDetails.periodStart,
            periodEnd = plate.commonDetails.periodEnd,
            year = plate.commonDetails.year,
            // UniqueDetails
            //regNo = plate.uniqueDetails.regNo ?: "",
            //imagePath = plate.uniqueDetails.imagePath,
            notes = plate.uniqueDetails.notes,
            vehicle = plate.uniqueDetails.vehicle,
            date = plate.uniqueDetails.date,
            cost = plate.uniqueDetails.cost,
            value = plate.uniqueDetails.value,
            status = plate.uniqueDetails.status,
            // Size
            width = plate.size.width,
            height = plate.size.height,
            weight = plate.size.weight,
            // Color
            colorMain = plate.color.main,
            colorSecondary = plate.color.secondary,
            // Source
            sourceName = plate.source.name,
            sourceAlias = plate.source.alias,
            sourceType = plate.source.type,
            sourceCountry = plate.source.country,
            sourceDetails = plate.source.details
        )
    }
    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
    try {
        val writer = FileWriter(file)
        /*val mappingStrategy = HeaderColumnNameMappingStrategy<CsvPlate>()
        mappingStrategy.type = CsvPlate::class.java
        val columnOrder = arrayOf(
            "id", "reg_no", "country", "region_1st", "region_2nd", "region_3rd",
            "type", "period_start", "period_end", "year",
            "notes", "vehicle", "date", "cost", "value", "status",
            "width", "height", "weight", "color_main", "color_secondary",
            "source_name", "source_alias", "source_type", "source_country", "source_details",
            "image_path"
        )
        mappingStrategy.setColumnOrderOnWrite(java.util.Comparator.comparingInt { header ->
            columnOrder.indexOf(header)
        })*/
        val beanToCsv = StatefulBeanToCsvBuilder<CsvPlate>(writer)
            .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
            //.withMappingStrategy(mappingStrategy)
            .build()
        beanToCsv.write(csvPlateList)
        writer.close()
        Log.d("CSV export", "File saved to : ${file.absolutePath}")
    } catch (e: IOException) {
        Log.e("CSV export", "Error writing to file", e)
    }
}

fun importPlatesFromCsv(context: Context, uri: Uri): List<Plate>? {
    try {
        val inputStream = context.contentResolver.openInputStream(uri) ?:
            throw IOException("Failed to open input stream for URI: $uri")
        val reader = BufferedReader(InputStreamReader(inputStream))
        val csvReader = CSVReaderBuilder(reader).withSkipLines(1).build()
        val csvData = csvReader.readAll()
        Log.d("CSV import", "CSV data: ${csvData.size}")
        val headerRow = listOf(
            /*"id", */"reg_no", "country", "region_1st", "region_2nd", "region_3rd", "type",
            "period_start", "period_end", "year", /*"reg_no", "image_path", */"notes", "vehicle",
            "date", "cost", "value", "status", "width", "height", "weight", "color_main",
            "color_secondary", "source_name", "source_alias", "source_type", "source_country",
            "source_details"
        )
        val plates = csvData.mapNotNull { row ->
            val csvPlate = mapRowToCsvPlate(row, headerRow)
            if (csvPlate != null) {
                Plate(
                    id = 0,
                    commonDetails = CommonDetails(
                        country = csvPlate.country,
                        region1st = csvPlate.region1st?.takeIf { it.isNotBlank() },
                        region2nd = csvPlate.region2nd?.takeIf { it.isNotBlank() },
                        region3rd = csvPlate.region3rd?.takeIf { it.isNotBlank() },
                        type = csvPlate.type,
                        periodStart = csvPlate.periodStart?.takeIf { it.isValidYear() },
                        periodEnd = csvPlate.periodEnd?.takeIf { it.isValidYear() },
                        year = csvPlate.year?.takeIf { it.isValidYear() }
                    ),
                    uniqueDetails = UniqueDetails(
                        regNo = csvPlate.regNo,
                        imagePath = null,
                        notes = csvPlate.notes?.takeIf { it.isNotBlank() },
                        vehicle = csvPlate.vehicle?.takeIf { it.isNotBlank() },
                        date = csvPlate.date?.takeIf { it.isNotBlank() },
                        cost = csvPlate.cost,
                        value = csvPlate.value,
                        status = csvPlate.status?.takeIf { it.isNotBlank() }
                    ),
                    size = Size(
                        width = csvPlate.width,
                        height = csvPlate.height,
                        weight = csvPlate.weight
                    ),
                    color = Color(
                        main = csvPlate.colorMain?.takeIf { it.isNotBlank() },
                        secondary = csvPlate.colorSecondary?.takeIf { it.isNotBlank() }
                    ),
                    source = Source(
                        name = csvPlate.sourceName?.takeIf { it.isNotBlank() },
                        alias = csvPlate.sourceAlias?.takeIf { it.isNotBlank() },
                        type = csvPlate.sourceType?.takeIf { it.isNotBlank() },
                        details = csvPlate.sourceDetails?.takeIf { it.isNotBlank() },
                        country = csvPlate.sourceCountry?.takeIf { it.isNotBlank() }
                    )
                )
            } else null
        }
        return plates
    } catch (e: IOException) {
        Log.e("CSV import", "Error reading from file", e)
        return null
    }
}

fun mapRowToCsvPlate(row: Array<String>, headerRow: List<String>): CsvPlate {
    val data = row.toList()
    return CsvPlate(
        //id = data.getOrNull(headerRow.indexOf("id"))?.toIntOrNull() ?: 0,
        regNo = data.getOrNull(headerRow.indexOf("reg_no")) ?: "",
        country = data.getOrNull(headerRow.indexOf("country")) ?: "",
        region1st = data.getOrNull(headerRow.indexOf("region_1st")),
        region2nd = data.getOrNull(headerRow.indexOf("region_2nd")),
        region3rd = data.getOrNull(headerRow.indexOf("region_3rd")),
        type = data.getOrNull(headerRow.indexOf("type")) ?: "",
        periodStart = data.getOrNull(headerRow.indexOf("period_start"))?.toIntOrNull(),
        periodEnd = data.getOrNull(headerRow.indexOf("period_end"))?.toIntOrNull(),
        year = data.getOrNull(headerRow.indexOf("year"))?.toIntOrNull(),
        //regNo = data.getOrNull(headerRow.indexOf("reg_no")) ?: "",
        //imagePath = data.getOrNull(headerRow.indexOf("image_path")),
        notes = data.getOrNull(headerRow.indexOf("notes")),
        vehicle = data.getOrNull(headerRow.indexOf("vehicle")),
        date = data.getOrNull(headerRow.indexOf("date")),
        cost = data.getOrNull(headerRow.indexOf("cost"))?.toLongOrNull(),
        value = data.getOrNull(headerRow.indexOf("value"))?.toLongOrNull(),
        status = data.getOrNull(headerRow.indexOf("status")),
        width = data.getOrNull(headerRow.indexOf("width"))?.toIntOrNull(),
        height = data.getOrNull(headerRow.indexOf("height"))?.toIntOrNull(),
        weight = data.getOrNull(headerRow.indexOf("weight"))?.toIntOrNull(),
        colorMain = data.getOrNull(headerRow.indexOf("color_main")),
        colorSecondary = data.getOrNull(headerRow.indexOf("color_secondary")),
        sourceName = data.getOrNull(headerRow.indexOf("source_name")),
        sourceAlias = data.getOrNull(headerRow.indexOf("source_alias")),
        sourceType = data.getOrNull(headerRow.indexOf("source_type")),
        sourceCountry = data.getOrNull(headerRow.indexOf("source_country")),
        sourceDetails = data.getOrNull(headerRow.indexOf("source_details")),
    )
}

data class CsvPlate(
    //@CsvBindByName(column = "id") val id: Int = 0,
    @CsvBindByName(column = "reg_no") val regNo: String = "",
    // CommonDetails
    @CsvBindByName(column = "country") val country: String = "",
    @CsvBindByName(column = "region_1st") val region1st: String? = null,
    @CsvBindByName(column = "region_2nd") val region2nd: String? = null,
    @CsvBindByName(column = "region_3rd") val region3rd: String? = null,
    @CsvBindByName(column = "type") val type: String = "",
    @CsvBindByName(column = "period_start") val periodStart: Int? = null,
    @CsvBindByName(column = "period_end") val periodEnd: Int? = null,
    @CsvBindByName(column = "year") val year: Int? = null,
    // UniqueDetails
    //@CsvBindByName(column = "reg_no") val regNo: String = "",
    //@CsvBindByName(column = "image_path") val imagePath: String? = null,
    @CsvBindByName(column = "notes") val notes: String? = null,
    @CsvBindByName(column = "vehicle") val vehicle: String? = null,
    @CsvBindByName(column = "date") val date: String? = null,
    @CsvBindByName(column = "cost") val cost: Long? = null,
    @CsvBindByName(column = "value") val value: Long? = null,
    @CsvBindByName(column = "status") val status: String? = null,
    // Size
    @CsvBindByName(column = "width") val width: Int? = null,
    @CsvBindByName(column = "height") val height: Int? = null,
    @CsvBindByName(column = "weight") val weight: Int? = null,
    // Color
    @CsvBindByName(column = "color_main") val colorMain: String? = null,
    @CsvBindByName(column = "color_secondary") val colorSecondary: String? = null,
    // Source
    @CsvBindByName(column = "source_name") val sourceName: String? = null,
    @CsvBindByName(column = "source_alias") val sourceAlias: String? = null,
    @CsvBindByName(column = "source_type") val sourceType: String? = null,
    @CsvBindByName(column = "source_country") val sourceCountry: String? = null,
    @CsvBindByName(column = "source_details") val sourceDetails: String? = null,
    // ArchivalDetails
    /*@CsvBindByName(column = "archival_date") val archivalDate: String? = null,
    @CsvBindByName(column = "recipient_name") val recipientName: String? = null,
    @CsvBindByName(column = "recipient_alias") val recipientAlias: String? = null,
    @CsvBindByName(column = "archival_reason") val archivalType: String? = null,
    @CsvBindByName(column = "archival_details") val archivalDetails: String? = null,
    @CsvBindByName(column = "price") val price: Long? = null,
    @CsvBindByName(column = "recipient_country") val recipientCountry: String? = null*/
)
