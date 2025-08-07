package com.mikohatara.collectioncatalog.util

import com.mikohatara.collectioncatalog.data.ItemDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InternalClipboardManager @Inject constructor() {

    private val _copiedItemDetails = MutableStateFlow<ItemDetails?>(null)
    val copiedItemDetails: StateFlow<ItemDetails?> = _copiedItemDetails.asStateFlow()

    fun copyItemDetails(itemDetails: ItemDetails) {
        _copiedItemDetails.value = itemDetails
    }

    fun clearCopiedItemDetails() {
        _copiedItemDetails.value = null
    }

    fun pasteItemDetails(): ItemDetails? {
        val itemDetails = _copiedItemDetails.value
        return itemDetails
    }

    fun canPasteItemDetails(): Boolean {
        return _copiedItemDetails.value != null
    }
}
