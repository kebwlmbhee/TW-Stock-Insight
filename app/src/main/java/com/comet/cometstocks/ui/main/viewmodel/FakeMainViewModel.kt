package com.comet.cometstocks.ui.main.viewmodel

import com.comet.cometstocks.data.model.StockAverage
import com.comet.cometstocks.data.model.StockBwi
import com.comet.cometstocks.data.model.StockDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeMainViewModel : StockViewModel {

    private val _stockDetailList = MutableStateFlow<List<StockDetail>?>(listOf(
        StockDetail(
            code = "2330",
            name = "台積電",
            tradeVolume = "1200000",
            tradeValue = "600000000",
            openingPrice = "600",
            highestPrice = "610",
            lowestPrice = "595",
            closingPrice = "605",
            change = "+5",
            transaction = "3000"
        ),
        StockDetail(
            code = "2317",
            name = "鴻海",
            tradeVolume = "800000",
            tradeValue = "320000000",
            openingPrice = "400",
            highestPrice = "405",
            lowestPrice = "395",
            closingPrice = "398",
            change = "-2",
            transaction = "2500"
        )
    ))
    override val stockDetailList: StateFlow<List<StockDetail>?> = _stockDetailList

    private val _stockAverageList = MutableStateFlow<List<StockAverage>?>(listOf(
        StockAverage(
            code = "2330",
            name = "台積電",
            closingPrice = "605",
            monthlyAveragePrice = "598"
        ),
        StockAverage(
            code = "2317",
            name = "鴻海",
            closingPrice = "402",
            monthlyAveragePrice = "395"
        )
    ))
    override val stockAverageList: StateFlow<List<StockAverage>?> = _stockAverageList

    private val _stockBwiList = MutableStateFlow<List<StockBwi>?>(listOf(
        StockBwi(
            code = "2330",
            name = "台積電",
            peRatio = "25.3",
            dividendYield = "2.5",
            pbRatio = "5.2"
        ),
        StockBwi(
            code = "2317",
            name = "鴻海",
            peRatio = "15.8",
            dividendYield = "3.2",
            pbRatio = "2.1"
        )
    ))
    override val stockBwiList: StateFlow<List<StockBwi>?> = _stockBwiList
    private val _currentSortOrder = MutableStateFlow(SortOrder.ORIGINAL)

    override val currentSortOrder: StateFlow<SortOrder> = _currentSortOrder
    override fun loadSortOrder() {}

    override fun sortStockListByCode(sortOrder: SortOrder) {}

    override suspend fun fetchAllConcurrently() {}
}