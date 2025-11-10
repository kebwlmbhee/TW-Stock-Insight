package com.comet.cometstocks.ui.main.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.comet.cometstocks.data.model.StockAverage
import com.comet.cometstocks.data.model.StockBwi
import com.comet.cometstocks.data.model.StockDetail
import com.comet.cometstocks.data.model.StockWithCode
import com.comet.cometstocks.data.repository.PreferenceRepository
import com.comet.cometstocks.data.repository.StockRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class SortOrder { ORIGINAL, ASC, DESC }
class MainViewModel(application: Application)
    : AndroidViewModel(application = application), StockViewModel {
    companion object {
        private val TAG = MainViewModel::class.qualifiedName
    }
    private val preferenceRepository = PreferenceRepository(application)
    private val _currentSortOrder = MutableStateFlow(SortOrder.ORIGINAL)
    override val currentSortOrder = _currentSortOrder.asStateFlow()

    private val stockRepo = StockRepository()
    private val _stockDetailList = MutableStateFlow<List<StockDetail>?>(null)
    override val stockDetailList = _stockDetailList.asStateFlow()
    private val _stockAverageList = MutableStateFlow<List<StockAverage>?>(null)
    override val stockAverageList = _stockAverageList.asStateFlow()
    private val _stockBwiList = MutableStateFlow<List<StockBwi>?>(null)
    override val stockBwiList = _stockBwiList.asStateFlow()

    // async must be called from a CoroutineScope
    override suspend fun fetchConcurrently() = coroutineScope {
        val bwi = async { stockRepo.getStockBwi() }
        val average = async { stockRepo.getStockAverage() }
        val detail = async { stockRepo.getStockDetail() }

        _stockBwiList.value = sortList(bwi.await())
        _stockAverageList.value = sortList(average.await())
        _stockDetailList.value = sortList(detail.await())
    }

    override fun loadSortOrder() {
        _currentSortOrder.value = preferenceRepository.getSortOrder()
    }

    override fun sortStockListByCode(sortOrder: SortOrder) {
        _currentSortOrder.value = if (currentSortOrder.value == sortOrder) {
            SortOrder.ORIGINAL
        } else {
            sortOrder
        }
        preferenceRepository.setSortOrder(currentSortOrder.value)
        viewModelScope.launch {
            fetchConcurrently()
        }
    }

    private fun <T: StockWithCode> sortList(list: List<T>?): List<T>? {
        if (list == null) return null
        return when (currentSortOrder.value) {
            SortOrder.ORIGINAL -> list
            SortOrder.ASC -> list.sortedBy { it.code }
            SortOrder.DESC -> list.sortedByDescending { it.code }
        }
    }
}