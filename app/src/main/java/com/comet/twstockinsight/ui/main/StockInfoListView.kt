package com.comet.twstockinsight.ui.main

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.comet.twstockinsight.R
import com.comet.twstockinsight.data.model.StockAverage
import com.comet.twstockinsight.data.model.StockBwi
import com.comet.twstockinsight.data.model.StockDetail
import com.comet.twstockinsight.ui.main.MainActivity.Companion.NO_DATA
import com.comet.twstockinsight.ui.main.viewmodel.FakeMainViewModel
import com.comet.twstockinsight.ui.theme.TWStockInsightTheme

class StockInfoListView {
    @Composable
    fun StockInfoList(stockDetailList: List<StockDetail>?,
                      stockAverageList: List<StockAverage>?,
                      stockBwiList: List<StockBwi>?,
                      modifier: Modifier = Modifier) {
        val TAG = "StockInfoList"
        val size = stockDetailList?.size ?: 0
        Log.d(TAG, "StockInfoList: size: $size")

        val showDialog = remember { mutableStateOf(false) }
        val selectedName = remember { mutableStateOf(null as String?) }
        val selectedBwi = remember { mutableStateOf<StockBwi?>(null) }
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(Color.LightGray),
            contentPadding = PaddingValues(vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(size) { item ->
                StockCardView().StockCard(
                    stockDetailList?.get(item),
                    stockAverageList?.get(item),
                    onClickBwi = { stockCode, stockName ->
                        // find first matched stock or null
                        val matchedBwi = stockBwiList?.firstOrNull {
                            it.code == stockCode
                        }
                        selectedName.value = stockName
                        selectedBwi.value = matchedBwi
                        showDialog.value = true
                    })
            }
        }

        // AlertDialog showing Bwi content
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                confirmButton = {
                    TextButton(onClick = { showDialog.value = false }) {
                        Text("OK")
                    }
                },
                title = { Text(
                    text = selectedName.value ?: NO_DATA
                ) },
                text = {
                    Text(
                        text = buildString {
                            val bwiValue = selectedBwi.value
                            append(stringResource(
                                R.string.stock_dividend,
                                bwiValue?.dividendYield.takeIf { !it.isNullOrBlank() }
                                    ?: stringResource(R.string.not_provided))
                            )
                                .append("\n")
                                .append(stringResource(
                                    R.string.stock_pe,
                                    bwiValue?.peRatio.takeIf { !it.isNullOrBlank() }
                                        ?: stringResource(R.string.not_provided))
                                )
                                .append("\n")
                                .append(stringResource(
                                    R.string.stock_pb,
                                    bwiValue?.pbRatio.takeIf { !it.isNullOrBlank() }
                                        ?: stringResource(R.string.not_provided))
                                )
                        }
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StockInfoListViewPreview() {
    val mainViewModel = FakeMainViewModel()
    val stockDetailList by mainViewModel.stockDetailList.collectAsState()
    val stockAverageList by mainViewModel.stockAverageList.collectAsState()
    val stockBwiList by mainViewModel.stockBwiList.collectAsState()
    TWStockInsightTheme {
        StockInfoListView().StockInfoList(stockDetailList,
            stockAverageList, stockBwiList)
    }
}