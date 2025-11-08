package com.comet.twstockinsight

import androidx.lifecycle.ViewModel
import com.comet.twstockinsight.data.model.StockAverage
import com.comet.twstockinsight.data.model.StockBwi
import com.comet.twstockinsight.data.model.StockDetail
import com.comet.twstockinsight.data.repository.StockRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    private val stockRepo = StockRepository()
    private val _stockDetailList = MutableStateFlow<List<StockDetail>?>(null)
    val stockDetailList = _stockDetailList.asStateFlow()
    private val _stockAverageList = MutableStateFlow<List<StockAverage>?>(null)
    val stockAverageList = _stockAverageList.asStateFlow()
    private val _stockBwiList = MutableStateFlow<List<StockBwi>?>(null)
    val stockBwiList = _stockBwiList.asStateFlow()

    suspend fun fetchStockBwi() {
        _stockBwiList.value = stockRepo.getStockBwi()
    }
    suspend fun fetchStockAverage() {
        _stockAverageList.value = stockRepo.getStockAverage()
    }
    suspend fun fetchStockDetail() {
        _stockDetailList.value = stockRepo.getStockDetail()
    }

    suspend fun fetchAll() {
        fetchStockDetail()
        fetchStockAverage()
        fetchStockBwi()
    }
}