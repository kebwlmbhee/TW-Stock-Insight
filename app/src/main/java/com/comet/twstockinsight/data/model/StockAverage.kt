package com.comet.twstockinsight.data.model

import kotlinx.serialization.SerialName


// exchangeReport/STOCK_DAY_AVG_ALL
data class StockAverage(
    @SerialName("Code") val code: String,
    @SerialName("Name") val name: String,
    @SerialName("ClosingPrice") val closingPrice: String,
    @SerialName("MonthlyAveragePrice") val monthlyAveragePrice: String)