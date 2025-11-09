package com.comet.cometstocks.data.model

import com.google.gson.annotations.SerializedName

// exchangeReport/BWIBBU_ALL
data class StockBwi(
    @SerializedName("Code") override val code: String,
    @SerializedName("Name") val name: String,
    @SerializedName("PEratio") val peRatio: String,
    @SerializedName("DividendYield") val dividendYield: String,
    @SerializedName("PBratio") val pbRatio: String
) : StockWithCode