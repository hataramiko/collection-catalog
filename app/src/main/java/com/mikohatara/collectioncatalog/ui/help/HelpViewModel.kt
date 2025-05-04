package com.mikohatara.collectioncatalog.ui.help

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.HELP_PAGE
import com.mikohatara.collectioncatalog.util.exportImportTemplateToCsv
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.OutputStreamWriter
import javax.inject.Inject

data class HelpUiState(
    val helpPage: HelpPage = HelpPage.DEFAULT,
    val isDownloading: Boolean = false,
    val downloadResult: DownloadResult? = null
)

sealed class DownloadResult {
    data class Success(val message: String) : DownloadResult()
    data class Failure(val message: String) : DownloadResult()
}

@HiltViewModel
class HelpViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val helpPage: HelpPage =
        savedStateHandle.get<String>(HELP_PAGE)?.let { HelpPage.valueOf(it) } ?: HelpPage.DEFAULT

    private val _uiState = MutableStateFlow< HelpUiState?>(null)
    val uiState: StateFlow<HelpUiState?> = _uiState.asStateFlow()

    private val importFirstRowExample = "reg_no\ncountry\nregion_1st\nregion_2nd\nregion_3rd\n" +
        "type\nperiod_start\nperiod_end\nyear\nnotes\nvehicle\ndate\ncost\nvalue\nstatus\n" +
        "width\nheight\nweight\ncolor_main\ncolor_secondary\n" +
        "source_name\nsource_alias\nsource_type\nsource_country\nsource_details"
    private val importFirstRowString = "reg_no,country,region_1st,region_2nd,region_3rd," +
        "type,period_start,period_end,year,notes,vehicle,date,cost,value,status," +
        "width,height,weight,color_main,color_secondary," +
        "source_name,source_alias,source_type,source_country,source_details"
    private val importEmptyRowString = ",,,,,,,,,,,,,,,,,,,,,,,,"

    init {
        viewModelScope.launch {
            when (helpPage) {
                HelpPage.DEFAULT -> _uiState.value = HelpUiState(helpPage = HelpPage.DEFAULT)
                HelpPage.IMPORT -> _uiState.value = HelpUiState(helpPage = HelpPage.IMPORT)
            }
        }
    }

    fun copyImportFirstRowToClipboard(context: Context) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("ImportFirstRow", importFirstRowString)
        clipboard.setPrimaryClip(clip)
    }

    fun copyImportEmptyRowToClipboard(context: Context) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("ImportEmptyRow", importEmptyRowString)
        clipboard.setPrimaryClip(clip)
    }

    fun downloadImportTemplate(context: Context, uri: Uri) {
        _uiState.update { it?.copy(isDownloading = true, downloadResult = null) }

        viewModelScope.launch {
            try {
                val contentResolver = context.contentResolver
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    OutputStreamWriter(outputStream).use { writer ->
                        exportImportTemplateToCsv(writer)
                    }
                }
                _uiState.update { it?.copy(
                    isDownloading = false,
                    downloadResult = DownloadResult.Success(getDownloadMessage(true, context))
                ) }
            } catch (e: Exception) {
                Log.e("HelpViewModel, download import template", "Download failed", e)
                _uiState.update { it?.copy(
                    isDownloading = false,
                    downloadResult = DownloadResult.Failure(getDownloadMessage(false, context))
                ) }
            }
        }
    }

    fun clearDownloadResult() {
        _uiState.update { it?.copy(isDownloading = false, downloadResult = null) }
    }

    private fun getDownloadMessage(isSuccess: Boolean, context: Context): String {
        val stringResId = if (isSuccess) {
            R.string.download_msg_success
        } else R.string.download_msg_failure
        return context.getString(stringResId)
    }
}

enum class HelpPage {
    DEFAULT,
    IMPORT
}
