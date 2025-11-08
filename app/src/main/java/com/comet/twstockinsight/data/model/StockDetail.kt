package com.comet.twstockinsight.data.model

import kotlinx.serialization.SerialName

// exchangeReport/STOCK_DAY_ALL
data class StockDetail(
    @SerialName("Code") val code: String,
    @SerialName("Name") val name: String,
    @SerialName("TradeVolume") val tradeVolume: String,
    @SerialName("TradeValue") val tradeValue: String,
    @SerialName("OpeningPrice") val openingPrice: String,
    @SerialName("HighestPrice") val highestPrice: String,
    @SerialName("LowestPrice") val lowestPrice: String,
    @SerialName("ClosingPrice") val closingPrice: String,
    @SerialName("Change") val change: String,
    @SerialName("Transaction") val transaction: String)