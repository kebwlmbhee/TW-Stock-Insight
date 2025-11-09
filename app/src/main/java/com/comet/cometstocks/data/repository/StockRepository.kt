package com.comet.cometstocks.data.repository

import com.comet.cometstocks.data.model.StockAverage
import com.comet.cometstocks.data.model.StockBwi
import com.comet.cometstocks.data.model.StockDetail
import com.comet.cometstocks.data.network.StockApiClient

class StockRepository {
    suspend fun getStockBwi(): List<StockBwi> {
        return StockApiClient.api.getStockBwi()
    }

    suspend fun getStockAverage(): List<StockAverage> {
        return StockApiClient.api.getStockAverage()
    }

    suspend fun getStockDetail(): List<StockDetail> {
        return StockApiClient.api.getStockDetail()
    }
}