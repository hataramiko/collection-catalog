package com.mikohatara.collectioncatalog.util

import android.content.Context
import android.net.Uri
import android.util.Log
import com.mikohatara.collectioncatalog.data.ArchivalDetails
import com.mikohatara.collectioncatalog.data.Color
import com.mikohatara.collectioncatalog.data.CommonDetails
import com.mikohatara.collectioncatalog.data.FormerPlate
import com.mikohatara.collectioncatalog.data.ItemDetails
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.Size
import com.mikohatara.collectioncatalog.data.Source
import com.mikohatara.collectioncatalog.data.UniqueDetails
import com.mikohatara.collectioncatalog.data.WantedPlate
import com.opencsv.CSVReaderBuilder
import com.opencsv.CSVWriter
import com.opencsv.bean.ColumnPositionMappingStrategy
import com.opencsv.bean.CsvBindByName
import com.opencsv.bean.StatefulBeanToCsvBuilder
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.Calendar

fun exportItemDetailsToCsv(
    writer: OutputStreamWriter,
    itemDetailsList: List<ItemDetails>,
    currencyFractions: Int,
    lengthUnitFractions: Int,
    weightUnitFractions: Int
) {
    val csvItemDetailsList = itemDetailsList.map { itemDetails ->
        CsvPlate(
            //id = itemDetails.id ?: 0, not used for export
            regNo = itemDetails.regNo, // this is part of UniqueDetails
            // CommonDetails
            country = itemDetails.country,
            region1st = itemDetails.region1st,
            region2nd = itemDetails.region2nd,
            region3rd = itemDetails.region3rd,
            type = itemDetails.type,
            periodStart = itemDetails.periodStart,
            periodEnd = itemDetails.periodEnd,
            year = itemDetails.year,
            // UniqueDetails
            //imagePath = plate.uniqueDetails.imagePath, not used for export
            notes = itemDetails.notes,
            vehicle = itemDetails.vehicle,
            date = itemDetails.date,
            cost = itemDetails.cost.toExportNumeralString(currencyFractions, true),
            value = itemDetails.value.toExportNumeralString(currencyFractions, true),
            status = itemDetails.status,
            // Size
            width = itemDetails.width.toExportNumeralString(lengthUnitFractions),
            height = itemDetails.height.toExportNumeralString(lengthUnitFractions),
            weight = itemDetails.weight.toExportNumeralString(weightUnitFractions),
            // Color
            colorMain = itemDetails.colorMain,
            colorSecondary = itemDetails.colorSecondary,
            // Source
            sourceName = itemDetails.sourceName,
            sourceAlias = itemDetails.sourceAlias,
            sourceType = itemDetails.sourceType,
            sourceCountry = itemDetails.sourceCountry,
            sourceDetails = itemDetails.sourceDetails,
            // ArchivalDetails
            archivalDate = itemDetails.archivalDate,
            archivalType = itemDetails.archivalType,
            price = itemDetails.price.toExportNumeralString(currencyFractions, true),
            recipientName = itemDetails.recipientName,
            recipientAlias = itemDetails.recipientAlias,
            recipientCountry = itemDetails.recipientCountry,
            archivalDetails = itemDetails.archivalDetails
        )
    }

    try {
        val mappingStrategy = ColumnPositionMappingStrategy<CsvPlate>()
        mappingStrategy.type = CsvPlate::class.java

        val columnOrder = arrayOf(
            "regNo", "country", "region1st", "region2nd", "region3rd",
            "type", "periodStart", "periodEnd", "year",
            "notes", "vehicle", "date", "cost", "value", "status",
            "width", "height", "weight", "colorMain", "colorSecondary",
            "sourceName", "sourceAlias", "sourceType", "sourceCountry", "sourceDetails",
            "archivalDate", "archivalType", "price",
            "recipientName","recipientAlias", "recipientCountry", "archivalDetails"
        )

        mappingStrategy.setColumnMapping(*columnOrder)
        val headerRow = columnOrder.map { it.toSnakeCase() }.toTypedArray()

        CSVWriter(writer).use { csvWriter ->
            csvWriter.writeNext(headerRow, true)
            val beanToCsv = StatefulBeanToCsvBuilder<CsvPlate>(writer)
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .withMappingStrategy(mappingStrategy)
                .build()
            beanToCsv.write(csvItemDetailsList)
        }
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
            "reg_no", "country", "region_1st", "region_2nd", "region_3rd",
            "type", "period_start", "period_end", "year",
            "notes", "vehicle", "date", "cost", "value", "status",
            "width", "height", "weight", "color_main", "color_secondary",
            "source_name", "source_alias", "source_type", "source_country", "source_details",
            "archival_date", "archival_type", "price",
            "recipient_name","recipient_alias", "recipient_country", "archival_details"
        )
        val plates = csvData.mapNotNull { row ->
            val csvPlate = mapRowToCsvPlate(row, headerRow)
            Plate(
                id = 0,
                commonDetails = CommonDetails(
                    country = csvPlate.country?.takeIf { it.isNotBlank() },
                    region1st = csvPlate.region1st?.takeIf { it.isNotBlank() },
                    region2nd = csvPlate.region2nd?.takeIf { it.isNotBlank() },
                    region3rd = csvPlate.region3rd?.takeIf { it.isNotBlank() },
                    type = csvPlate.type?.takeIf { it.isNotBlank() },
                    periodStart = csvPlate.periodStart?.takeIf { it.isValidYear() },
                    periodEnd = csvPlate.periodEnd?.takeIf { it.isValidYear() },
                    year = csvPlate.year?.takeIf { it.isValidYear() }
                ),
                uniqueDetails = UniqueDetails(
                    regNo = csvPlate.regNo?.takeIf { it.isNotBlank() },
                    imagePath = null,
                    notes = csvPlate.notes?.takeIf { it.isNotBlank() },
                    vehicle = csvPlate.vehicle?.takeIf { it.isNotBlank() },
                    date = csvPlate.date?.takeIf { it.isNotBlank() },
                    cost = csvPlate.cost?.toCurrencyLongOrNull(),
                    value = csvPlate.value?.toCurrencyLongOrNull(),
                    status = csvPlate.status?.takeIf { it.isNotBlank() }
                ),
                size = Size(
                    width = csvPlate.width?.toMeasurementIntOrNull(),
                    height = csvPlate.height?.toMeasurementIntOrNull(),
                    weight = csvPlate.weight?.toMeasurementIntOrNull()
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
        }
        return plates
    } catch (e: IOException) {
        Log.e("CSV import", "Error reading from file", e)
        return null
    }
}

fun importWantedPlatesFromCsv(context: Context, uri: Uri): List<WantedPlate>? {
    try {
        val inputStream = context.contentResolver.openInputStream(uri) ?:
        throw IOException("Failed to open input stream for URI: $uri")
        val reader = BufferedReader(InputStreamReader(inputStream))
        val csvReader = CSVReaderBuilder(reader).withSkipLines(1).build()
        val csvData = csvReader.readAll()
        Log.d("CSV import", "CSV data: ${csvData.size}")
        val headerRow = listOf(
            "reg_no", "country", "region_1st", "region_2nd", "region_3rd",
            "type", "period_start", "period_end", "year",
            "notes", "vehicle", "date", "cost", "value", "status",
            "width", "height", "weight", "color_main", "color_secondary",
            "source_name", "source_alias", "source_type", "source_country", "source_details",
            "archival_date", "archival_type", "price",
            "recipient_name","recipient_alias", "recipient_country", "archival_details"
        )
        val wantedPlates = csvData.mapNotNull { row ->
            val csvPlate = mapRowToCsvPlate(row, headerRow)
            WantedPlate(
                id = 0,
                regNo = csvPlate.regNo?.takeIf { it.isNotBlank() },
                imagePath = null,
                notes = csvPlate.notes?.takeIf { it.isNotBlank() },
                commonDetails = CommonDetails(
                    country = csvPlate.country?.takeIf { it.isNotBlank() },
                    region1st = csvPlate.region1st?.takeIf { it.isNotBlank() },
                    region2nd = csvPlate.region2nd?.takeIf { it.isNotBlank() },
                    region3rd = csvPlate.region3rd?.takeIf { it.isNotBlank() },
                    type = csvPlate.type?.takeIf { it.isNotBlank() },
                    periodStart = csvPlate.periodStart?.takeIf { it.isValidYear() },
                    periodEnd = csvPlate.periodEnd?.takeIf { it.isValidYear() },
                    year = csvPlate.year?.takeIf { it.isValidYear() }
                ),
                size = Size(
                    width = csvPlate.width?.toMeasurementIntOrNull(),
                    height = csvPlate.height?.toMeasurementIntOrNull(),
                    weight = csvPlate.weight?.toMeasurementIntOrNull()
                ),
                color = Color(
                    main = csvPlate.colorMain?.takeIf { it.isNotBlank() },
                    secondary = csvPlate.colorSecondary?.takeIf { it.isNotBlank() }
                )
            )
        }
        return wantedPlates
    } catch (e: IOException) {
        Log.e("CSV import", "Error reading from file", e)
        return null
    }
}

fun importFormerPlatesFromCsv(context: Context, uri: Uri): List<FormerPlate>? {
    try {
        val inputStream = context.contentResolver.openInputStream(uri) ?:
        throw IOException("Failed to open input stream for URI: $uri")
        val reader = BufferedReader(InputStreamReader(inputStream))
        val csvReader = CSVReaderBuilder(reader).withSkipLines(1).build()
        val csvData = csvReader.readAll()
        Log.d("CSV import", "CSV data: ${csvData.size}")
        val headerRow = listOf(
            "reg_no", "country", "region_1st", "region_2nd", "region_3rd",
            "type", "period_start", "period_end", "year",
            "notes", "vehicle", "date", "cost", "value", "status",
            "width", "height", "weight", "color_main", "color_secondary",
            "source_name", "source_alias", "source_type", "source_country", "source_details",
            "archival_date", "archival_type", "price",
            "recipient_name","recipient_alias", "recipient_country", "archival_details"
        )
        val formerPlates = csvData.mapNotNull { row ->
            val csvPlate = mapRowToCsvPlate(row, headerRow)
            FormerPlate(
                id = 0,
                commonDetails = CommonDetails(
                    country = csvPlate.country?.takeIf { it.isNotBlank() },
                    region1st = csvPlate.region1st?.takeIf { it.isNotBlank() },
                    region2nd = csvPlate.region2nd?.takeIf { it.isNotBlank() },
                    region3rd = csvPlate.region3rd?.takeIf { it.isNotBlank() },
                    type = csvPlate.type?.takeIf { it.isNotBlank() },
                    periodStart = csvPlate.periodStart?.takeIf { it.isValidYear() },
                    periodEnd = csvPlate.periodEnd?.takeIf { it.isValidYear() },
                    year = csvPlate.year?.takeIf { it.isValidYear() }
                ),
                uniqueDetails = UniqueDetails(
                    regNo = csvPlate.regNo?.takeIf { it.isNotBlank() },
                    imagePath = null,
                    notes = csvPlate.notes?.takeIf { it.isNotBlank() },
                    vehicle = csvPlate.vehicle?.takeIf { it.isNotBlank() },
                    date = csvPlate.date?.takeIf { it.isNotBlank() },
                    cost = csvPlate.cost?.toCurrencyLongOrNull(),
                    value = null,
                    status = null
                ),
                size = Size(
                    width = csvPlate.width?.toMeasurementIntOrNull(),
                    height = csvPlate.height?.toMeasurementIntOrNull(),
                    weight = csvPlate.weight?.toMeasurementIntOrNull()
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
                ),
                archivalDetails = ArchivalDetails(
                    archivalDate = csvPlate.archivalDate?.takeIf { it.isNotBlank() },
                    recipientName = csvPlate.recipientName?.takeIf { it.isNotBlank() },
                    recipientAlias = csvPlate.recipientAlias?.takeIf { it.isNotBlank() },
                    archivalReason = csvPlate.archivalType?.takeIf { it.isNotBlank() },
                    archivalDetails = csvPlate.archivalDetails?.takeIf { it.isNotBlank() },
                    price = csvPlate.price?.toCurrencyLongOrNull(),
                    recipientCountry = csvPlate.recipientCountry?.takeIf { it.isNotBlank() }
                )
            )
        }
        return formerPlates
    } catch (e: IOException) {
        Log.e("CSV import", "Error reading from file", e)
        return null
    }
}

fun mapRowToCsvPlate(row: Array<String>, headerRow: List<String>): CsvPlate {
    val data = row.toList()
    return CsvPlate(
        //id = data.getOrNull(headerRow.indexOf("id"))?.toIntOrNull() ?: 0,
        regNo = data.getOrNull(headerRow.indexOf("reg_no")),
        country = data.getOrNull(headerRow.indexOf("country")),
        region1st = data.getOrNull(headerRow.indexOf("region_1st")),
        region2nd = data.getOrNull(headerRow.indexOf("region_2nd")),
        region3rd = data.getOrNull(headerRow.indexOf("region_3rd")),
        type = data.getOrNull(headerRow.indexOf("type")),
        periodStart = data.getOrNull(headerRow.indexOf("period_start"))?.toIntOrNull(),
        periodEnd = data.getOrNull(headerRow.indexOf("period_end"))?.toIntOrNull(),
        year = data.getOrNull(headerRow.indexOf("year"))?.toIntOrNull(),
        //imagePath = data.getOrNull(headerRow.indexOf("image_path")),
        notes = data.getOrNull(headerRow.indexOf("notes")),
        vehicle = data.getOrNull(headerRow.indexOf("vehicle")),
        date = data.getOrNull(headerRow.indexOf("date")),
        cost = data.getOrNull(headerRow.indexOf("cost")),
        value = data.getOrNull(headerRow.indexOf("value")),
        status = data.getOrNull(headerRow.indexOf("status")),
        width = data.getOrNull(headerRow.indexOf("width")),
        height = data.getOrNull(headerRow.indexOf("height")),
        weight = data.getOrNull(headerRow.indexOf("weight")),
        colorMain = data.getOrNull(headerRow.indexOf("color_main")),
        colorSecondary = data.getOrNull(headerRow.indexOf("color_secondary")),
        sourceName = data.getOrNull(headerRow.indexOf("source_name")),
        sourceAlias = data.getOrNull(headerRow.indexOf("source_alias")),
        sourceType = data.getOrNull(headerRow.indexOf("source_type")),
        sourceCountry = data.getOrNull(headerRow.indexOf("source_country")),
        sourceDetails = data.getOrNull(headerRow.indexOf("source_details")),
        archivalDate = data.getOrNull(headerRow.indexOf("archival_date")),
        archivalType = data.getOrNull(headerRow.indexOf("archival_type")),
        price = data.getOrNull(headerRow.indexOf("price")),
        recipientName = data.getOrNull(headerRow.indexOf("recipient_name")),
        recipientAlias = data.getOrNull(headerRow.indexOf("recipient_alias")),
        recipientCountry = data.getOrNull(headerRow.indexOf("recipient_country")),
        archivalDetails = data.getOrNull(headerRow.indexOf("archival_details"))
    )
}

fun exportImportTemplateToCsv(writer: OutputStreamWriter) {
    val emptyCsvPlate = CsvPlate()

    try {
        val mappingStrategy = ColumnPositionMappingStrategy<CsvPlate>()
        mappingStrategy.type = CsvPlate::class.java

        val columnOrder = arrayOf(
            "regNo", "country", "region1st", "region2nd", "region3rd",
            "type", "periodStart", "periodEnd", "year",
            "notes", "vehicle", "date", "cost", "value", "status",
            "width", "height", "weight", "colorMain", "colorSecondary",
            "sourceName", "sourceAlias", "sourceType", "sourceCountry", "sourceDetails",
            "archivalDate", "archivalType", "price",
            "recipientName","recipientAlias", "recipientCountry", "archivalDetails"
        )

        mappingStrategy.setColumnMapping(*columnOrder)
        val headerRow = columnOrder.map { it.toSnakeCase() }.toTypedArray()

        CSVWriter(writer).use { csvWriter ->
            csvWriter.writeNext(headerRow, true)
            val beanToCsv = StatefulBeanToCsvBuilder<CsvPlate>(writer)
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .withMappingStrategy(mappingStrategy)
                .build()
            beanToCsv.write(emptyCsvPlate)
        }
    } catch (e: IOException) {
        Log.e("CSV export", "Error writing to file", e)
    }
}

fun getFileNameForExport(title: String): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = System.currentTimeMillis()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)
    val timestamp = "%04d-%02d-%02d_%02d%02d%02d".format(year, month, day, hour, minute, second)

    return "Rekkary_${title}_${timestamp}.csv"
}

// This might not work with digits not preceded by a lowercase letter
private fun String.toSnakeCase(): String = replace(Regex("([a-z])([A-Z0-9])"), "$1_$2").lowercase()

data class CsvPlate(
    //@CsvBindByName(column = "id") val id: Int = 0,
    @CsvBindByName(column = "reg_no") val regNo: String? = null, // this is part of UniqueDetails
        // CommonDetails
    @CsvBindByName(column = "country") val country: String? = null,
    @CsvBindByName(column = "region_1st") val region1st: String? = null,
    @CsvBindByName(column = "region_2nd") val region2nd: String? = null,
    @CsvBindByName(column = "region_3rd") val region3rd: String? = null,
    @CsvBindByName(column = "type") val type: String? = null,
    @CsvBindByName(column = "period_start") val periodStart: Int? = null,
    @CsvBindByName(column = "period_end") val periodEnd: Int? = null,
    @CsvBindByName(column = "year") val year: Int? = null,
        // UniqueDetails
    //@CsvBindByName(column = "image_path") val imagePath: String? = null,
    @CsvBindByName(column = "notes") val notes: String? = null,
    @CsvBindByName(column = "vehicle") val vehicle: String? = null,
    @CsvBindByName(column = "date") val date: String? = null,
    @CsvBindByName(column = "cost") val cost: String? = null,
    @CsvBindByName(column = "value") val value: String? = null,
    @CsvBindByName(column = "status") val status: String? = null,
        // Size
    @CsvBindByName(column = "width") val width: String? = null,
    @CsvBindByName(column = "height") val height: String? = null,
    @CsvBindByName(column = "weight") val weight: String? = null,
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
    @CsvBindByName(column = "archival_date") val archivalDate: String? = null,
    @CsvBindByName(column = "archival_reason") val archivalType: String? = null,
    @CsvBindByName(column = "price") val price: String? = null,
    @CsvBindByName(column = "recipient_name") val recipientName: String? = null,
    @CsvBindByName(column = "recipient_alias") val recipientAlias: String? = null,
    @CsvBindByName(column = "recipient_country") val recipientCountry: String? = null,
    @CsvBindByName(column = "archival_details") val archivalDetails: String? = null
)
