package com.comet.twstockinsight.ui.main.viewmodel

import com.comet.twstockinsight.data.model.StockAverage
import com.comet.twstockinsight.data.model.StockBwi
import com.comet.twstockinsight.data.model.StockDetail
import kotlinx.coroutines.flow.StateFlow

interface StockViewModel {
    val stockDetailList: StateFlow<List<StockDetail>?>
    val stockAverageList: StateFlow<List<StockAverage>?>
    val stockBwiList: StateFlow<List<StockBwi>?>
    val currentSortOrder: StateFlow<SortOrder>

    fun loadSortOrder()
    fun sortStockListByCode(sortOrder: SortOrder)
    suspend fun fetchAllConcurrently()
}