package com.comet.cometstocks.ui.main

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.comet.cometstocks.R
import com.comet.cometstocks.data.model.StockAverage
import com.comet.cometstocks.data.model.StockColorResult
import com.comet.cometstocks.data.model.StockDetail
import com.comet.cometstocks.ui.main.MainActivity.Companion.NO_DATA
import com.comet.cometstocks.ui.main.MainActivity.Companion.NO_NAME
import com.comet.cometstocks.ui.theme.Green
import com.comet.cometstocks.ui.theme.Red
import com.comet.cometstocks.ui.theme.CometStocksTheme

class StockCardView {
    @Composable
    fun StockCard(
        stockDetail: StockDetail?,
        stockAverage: StockAverage?,
        onClickBwi: ((String, String) -> Unit)? = null
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .clickable(
                    indication = LocalIndication.current,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onClickBwi?.invoke(stockDetail?.code ?: "", stockDetail?.name ?: "")
                },
            // shadow
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            StockTitle(stockDetail)
            StockPriceGrid(stockDetail, stockAverage)
            StockTransaction(stockDetail)
        }
    }

    @Composable
    fun StockTransaction(stockDetail: StockDetail?,
                         modifier: Modifier = Modifier) {
        val transaction = listOf(
            stringResource(R.string.transaction) to (stockDetail?.transaction.takeIf { !it.isNullOrBlank() } ?: NO_DATA),
            stringResource(R.string.trade_volume) to (stockDetail?.tradeVolume.takeIf { !it.isNullOrBlank() } ?: NO_DATA),
            stringResource(R.string.trade_value) to (stockDetail?.tradeValue.takeIf { !it.isNullOrBlank() } ?: NO_DATA)
        )

        Row(Modifier.padding(horizontal = 16.dp)) {
            for (column in 0 until 3) {
                Text(
                    text = "${transaction[column].first}: ${transaction[column].second}",
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1f)
                )
            }
        }
    }

    @Composable
    fun StockPriceGrid(stockDetail: StockDetail?,
                       stockAverage: StockAverage?,
                       modifier: Modifier = Modifier) {
        val details = listOf(
            stringResource(R.string.opening_price) to
                    (stockDetail?.openingPrice.takeIf { !it.isNullOrBlank() } ?: NO_DATA),
            stringResource(R.string.closing_price) to
                    (stockDetail?.closingPrice.takeIf { !it.isNullOrBlank() } ?: NO_DATA),
            stringResource(R.string.highest_price) to
                    (stockDetail?.highestPrice.takeIf { !it.isNullOrBlank() } ?: NO_DATA),
            stringResource(R.string.lowest_price) to
                    (stockDetail?.lowestPrice.takeIf { !it.isNullOrBlank() } ?: NO_DATA),
            stringResource(R.string.price_change) to
                    (stockDetail?.change.takeIf { !it.isNullOrBlank() } ?: NO_DATA),
            stringResource(R.string.monthly_average_price) to
                    (stockAverage?.monthlyAveragePrice.takeIf { !it.isNullOrBlank() } ?: NO_DATA),
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 20.dp)
        ) {
            for(row in 0 until 3) {
                Row {
                    for (column in 0 until 2) {
                        val index = row * 2 + column
                        val colorResult = getStockColors(stockDetail, stockAverage)
                        val color = when (index) {
                            1 -> {
                                colorResult.closePriceColor
                            }
                            4 -> {
                                colorResult.priceChangeColor
                            }
                            else -> {
                                Color.Unspecified
                            }
                        }
                        Text(
                            text = "${details[index].first}: ${details[index].second}",
                            fontSize = 12.sp,
                            color = color,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun getStockColors(
        stockDetail: StockDetail?,
        stockAverage: StockAverage?
    ): StockColorResult  {
        val closingPriceDouble = stockDetail?.closingPrice?.toDoubleOrNull() ?: 0.0
        val monthlyAveragePriceDouble = stockAverage?.monthlyAveragePrice?.toDoubleOrNull() ?: 0.0
        val closePriceColor = when {
            stockDetail?.closingPrice.isNullOrBlank() || stockAverage?.monthlyAveragePrice.isNullOrBlank() -> Color.Unspecified
            closingPriceDouble > monthlyAveragePriceDouble -> Red
            closingPriceDouble < monthlyAveragePriceDouble -> Green
            else -> Color.Unspecified
        }

        val priceChangeDouble = stockDetail?.change?.toDoubleOrNull() ?: 0.0
        val priceChangeColor = when {
            priceChangeDouble > 0 -> Red
            priceChangeDouble < 0.0 -> Green
            else -> Color.Unspecified
        }
        return StockColorResult(closePriceColor, priceChangeColor)
    }

    @Composable
    private fun StockTitle(stockDetail: StockDetail?,
                           modifier: Modifier = Modifier) {
        Column {
            Text(
                text = stockDetail?.code.takeIf { !it.isNullOrBlank() } ?: NO_DATA,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 8.dp)
            )
            Text(
                text = stockDetail?.name.takeIf { !it.isNullOrBlank() } ?: NO_NAME,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StockCardViewPreview() {
    CometStocksTheme {
        StockCardView().StockCard(null,
            null, null)
    }
}