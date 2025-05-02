package com.mikohatara.collectioncatalog.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.ui.home.SortBy

@Composable
fun getSortByText(sortBy: SortBy): String {
    return when (sortBy) {
        SortBy.COUNTRY_AND_TYPE_ASC -> stringResource(R.string.sort_by_country_and_type_asc)
        SortBy.COUNTRY_AND_TYPE_DESC -> stringResource(R.string.sort_by_country_and_type_desc)
        SortBy.AGE_ASC -> stringResource(R.string.sort_by_age_asc)
        SortBy.AGE_DESC -> stringResource(R.string.sort_by_age_desc)
        SortBy.START_DATE_NEWEST -> stringResource(R.string.sort_by_date_newest)
        SortBy.START_DATE_OLDEST -> stringResource(R.string.sort_by_date_oldest)
        SortBy.END_DATE_NEWEST -> stringResource(R.string.sort_by_archival_newest)
        SortBy.END_DATE_OLDEST -> stringResource(R.string.sort_by_archival_oldest)
    }
}
