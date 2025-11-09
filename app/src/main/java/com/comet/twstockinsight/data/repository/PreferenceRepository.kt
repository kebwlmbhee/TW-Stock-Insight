package com.comet.twstockinsight.data.repository

import android.content.Context
import com.comet.twstockinsight.ui.main.viewmodel.SortOrder
import androidx.core.content.edit

class PreferenceRepository(context: Context) {
    companion object {
        private const val PREFS_NAME = "prefs"
        private const val SORT_ORDER_KEY = "sort_order"
    }
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getSortOrder(): SortOrder {
        val saved = prefs.getString(SORT_ORDER_KEY, SortOrder.ORIGINAL.name)
        return SortOrder.valueOf(saved!!)
    }

    fun setSortOrder(sortOrder: SortOrder) {
        prefs.edit { putString(SORT_ORDER_KEY, sortOrder.name) }
    }
}