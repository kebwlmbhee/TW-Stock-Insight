package com.comet.twstockinsight.ui.main

import androidx.lifecycle.ViewModel
import com.comet.twstockinsight.data.model.StockAverage
import com.comet.twstockinsight.data.model.StockBwi
import com.comet.twstockinsight.data.model.StockDetail
import com.comet.twstockinsight.data.repository.StockRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    companion object {
        private val TAG = MainViewModel::class.qualifiedName
    }
    private val stockRepo = StockRepository()
    private val _stockDetailList = MutableStateFlow<List<StockDetail>?>(null)
    val stockDetailList = _stockDetailList.asStateFlow()
    private val _stockAverageList = MutableStateFlow<List<StockAverage>?>(null)
    val stockAverageList = _stockAverageList.asStateFlow()
    private val _stockBwiList = MutableStateFlow<List<StockBwi>?>(null)
    val stockBwiList = _stockBwiList.asStateFlow()

    // async must be called from a CoroutineScope
    suspend fun fetchAllConcurrently() = coroutineScope {
        while (true) {
            val bwi = async { stockRepo.getStockBwi() }
            val average = async { stockRepo.getStockAverage() }
            val detail = async { stockRepo.getStockDetail() }

            _stockBwiList.value = bwi.await()
            _stockAverageList.value = average.await()
            _stockDetailList.value = detail.await()
            delay(5000)
        }
    }
}