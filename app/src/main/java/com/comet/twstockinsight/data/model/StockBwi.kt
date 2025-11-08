package com.comet.twstockinsight.data.model

import kotlinx.serialization.SerialName

// exchangeReport/BWIBBU_ALL
data class StockBwi(
    @SerialName("Code") val code: String,
    @SerialName("Name") val name: String,
    @SerialName("PEratio") val peRatio: String,
    @SerialName("DividendYield") val dividendYield: String,
    @SerialName("PBratio") val pbRatio: String) {
}