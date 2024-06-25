package com.mikohatara.collectioncatalog.ui.home

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
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val sortByOptions = SortBy.entries.toList()

    init {
        getPlates()
    }

    fun getPlates() {
        plateRepository.getAllPlatesStream().onEach { items ->
            _uiState.value = _uiState.value.copy(items = items)
        }.launchIn(viewModelScope)
    }

    fun setSortBy(sortBy: SortBy) {
        val sortedItems = _uiState.value.items.sortedWith(compareBy
            { item ->
                when (sortBy) {
                    SortBy.COUNTRY_ASC -> item.commonDetails.country
                    SortBy.COUNTRY_DESC -> descendingStringComparator().compare(item.commonDetails.country, "") // TODO fix
                    SortBy.DATE_ASC -> item.uniqueDetails.date
                    SortBy.DATE_DESC -> item.uniqueDetails.date
                }
            }
        )
        _uiState.value = _uiState.value.copy(items = sortedItems, sortBy = sortBy)
    }

    private fun descendingStringComparator(): Comparator<String> {
        return compareBy { it.reversed() }
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
        Log.d("uistate list setSortBy", uiState.value.toString())
    }*/

    /*
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }*/
}

enum class SortBy {
    COUNTRY_ASC,
    COUNTRY_DESC,
    DATE_ASC,
    DATE_DESC
}
