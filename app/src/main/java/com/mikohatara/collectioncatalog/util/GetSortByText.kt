package com.mikohatara.collectioncatalog.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.ui.home.SortBy

@Composable
fun getSortByText(sortBy: SortBy): String {
    return when (sortBy) {
        SortBy.COUNTRY_ASC -> stringResource(R.string.sort_by_country_asc)
        SortBy.COUNTRY_DESC -> stringResource(R.string.sort_by_country_desc)
        SortBy.COUNTRY_AND_TYPE_ASC -> stringResource(R.string.sort_by_country_and_type_asc)
        SortBy.COUNTRY_AND_TYPE_DESC -> stringResource(R.string.sort_by_country_and_type_desc)
        SortBy.COUNTRY_AND_AGE_ASC -> stringResource(R.string.sort_by_country_and_age_asc)
        SortBy.COUNTRY_AND_AGE_DESC -> stringResource(R.string.sort_by_country_and_age_desc)
        SortBy.START_DATE_NEWEST -> stringResource(R.string.sort_by_date_newest)
        SortBy.START_DATE_OLDEST -> stringResource(R.string.sort_by_date_oldest)
        SortBy.END_DATE_NEWEST -> "Most recent archival" //TODO add stringRes
        SortBy.END_DATE_OLDEST -> "Earliest archival"
    }
}
