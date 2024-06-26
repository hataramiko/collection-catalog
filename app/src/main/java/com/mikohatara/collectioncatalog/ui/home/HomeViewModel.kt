package com.mikohatara.collectioncatalog.ui.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.PlateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class HomeUiState(
    val items: List<Plate> = emptyList(),
    val sortBy: SortBy = SortBy.COUNTRY_ASC
    //val isLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val plateRepository: PlateRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val showSortByBottomSheet = mutableStateOf(false)
    val showFilterBottomSheet = mutableStateOf(false)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val sortByOptions = SortBy.entries.toList()

    init {
        getPlates()
    }

    fun getPlates() {
        plateRepository.getAllPlatesStream().onEach { items ->
            _uiState.value = _uiState.value.copy(items = items)
            setSortBy(uiState.value.sortBy)
        }.launchIn(viewModelScope)
    }

    fun setSortBy(sortBy: SortBy) {
        val items = _uiState.value.items
        val sortedItems = when (sortBy) {
            SortBy.COUNTRY_ASC -> items.sortedWith(
                compareBy<Plate> { it.commonDetails.country }
                    .thenBy(nullsLast()) { it.commonDetails.region }
                    .thenBy(nullsLast()) { it.commonDetails.area }
            )
            SortBy.COUNTRY_DESC -> items.sortedWith(
                compareByDescending<Plate> { it.commonDetails.country }
                    .thenByDescending { it.commonDetails.region }
                    .thenByDescending { it.commonDetails.area }
            )
            SortBy.COUNTRY_AND_TYPE_ASC -> items.sortedWith(
                compareBy<Plate> { it.commonDetails.country }
                    .thenBy { it.commonDetails.type }
            )
            SortBy.COUNTRY_AND_TYPE_DESC -> items.sortedWith(
                compareByDescending<Plate> { it.commonDetails.country }
                    .thenByDescending { it.commonDetails.type }
            )
            SortBy.DATE_NEWEST -> items.sortedByDescending { it.uniqueDetails.date }
            SortBy.DATE_OLDEST -> items.sortedWith(
                compareBy(nullsLast()) { it.uniqueDetails.date }
            )
        }
        _uiState.value = _uiState.value.copy(items = sortedItems, sortBy = sortBy)
    }

    /*
    private val _sortBy = MutableStateFlow(SortBy.COUNTRY_DESC)
    val sortBy: StateFlow<SortBy> = _sortBy.asStateFlow()*/

    /*
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HomeUiState> =
        plateRepository.getAllPlatesByCountryAscStream()
            //.map { HomeUiState(it) }
            //.map { items -> HomeUiState(items.sortedBy { it.commonDetails.country }) }
            .flatMapLatest { items ->
                when (_sortBy.value) {
                    SortBy.COUNTRY_ASC -> flowOf(HomeUiState(items.sortedBy {
                        it.commonDetails.country
                    }))
                    SortBy.COUNTRY_DESC -> flowOf(HomeUiState(items.sortedByDescending {
                        it.commonDetails.country
                    }))
                    SortBy.DATE_ASC -> flowOf(HomeUiState(items.sortedBy {
                        it.uniqueDetails.date
                    }))
                    SortBy.DATE_DESC -> flowOf(HomeUiState(items.sortedByDescending {
                        it.uniqueDetails.date
                    }))
                    else -> flowOf(HomeUiState(items))
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )*/

    /*
    fun setSortBy(sortBy: SortBy) {
        _sortBy.value = sortBy
        Log.d("sort by; viewmodel", _sortBy.value.toString())
        Log.d("uiState list setSortBy", uiState.value.toString())
    }*/

    /*
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }*/
}

enum class SortBy {
    COUNTRY_ASC,
    COUNTRY_DESC,
    COUNTRY_AND_TYPE_ASC,
    COUNTRY_AND_TYPE_DESC,
    DATE_NEWEST,
    DATE_OLDEST
}
