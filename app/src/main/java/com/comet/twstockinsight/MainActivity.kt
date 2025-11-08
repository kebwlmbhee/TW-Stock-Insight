package com.comet.twstockinsight

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.comet.twstockinsight.data.model.StockAverage
import com.comet.twstockinsight.data.model.StockBwi
import com.comet.twstockinsight.data.model.StockDetail
import com.comet.twstockinsight.ui.theme.TWStockInsightTheme

class MainActivity : ComponentActivity() {

    companion object {
        private val TAG = MainActivity::class.qualifiedName
    }

    private val mMainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TWStockInsightTheme {
                MainScreen(mMainViewModel = mMainViewModel)
            }
        }
    }
}

@Composable
fun MainScreen(mMainViewModel: MainViewModel) {
    val stockDetailList = mMainViewModel.stockDetailList.collectAsState()
    val stockAverageList = mMainViewModel.stockAverageList.collectAsState()
    val stockBwiList = mMainViewModel.stockBwiList.collectAsState()

    // load data only first time
    LaunchedEffect(Unit) {
        mMainViewModel.fetchAll()
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        StockInfoList(
            stockDetailList.value,
            stockAverageList.value,
            stockBwiList.value,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun StockInfoList(stockDetailList: List<StockDetail>?,
                  stockAverageList: List<StockAverage>?,
                  stockBwiList: List<StockBwi>?,
                  modifier: Modifier = Modifier) {
    val TAG = "StockInfoList"
    val size = stockDetailList?.size ?: 0
    Log.d(TAG, "StockInfoList: size: $size")

    val showDialog = remember { mutableStateOf(false) }
    val selectedBwi = remember { mutableStateOf<StockBwi?>(null) }
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.LightGray),
        contentPadding = PaddingValues(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(size) { item ->
            StockCard(
                stockDetailList?.get(item),
                stockAverageList?.get(item),
                onClickBwi = { stockCode ->
                    val matchedBwi = stockBwiList?.firstOrNull { it.code == stockCode }
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
            title = { Text(text = selectedBwi.value?.name ?: "Bwi Info") },
            text = {
                Text(
                    "殖利率: ${selectedBwi.value?.dividendYield ?: "--"}\n" +
                            "本益比: ${selectedBwi.value?.peRatio ?: "--"}\n" +
                            "股價淨值比: ${selectedBwi.value?.pbRatio ?: "--"}"
                )
            }
        )
    }
}

@Composable
fun StockCard(
    stockDetail: StockDetail?,
    stockAverage: StockAverage?,
    onClickBwi: ((String) -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clickable(
                indication = LocalIndication.current,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClickBwi?.invoke(stockDetail?.code ?: "")
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
        "成交筆數" to (stockDetail?.transaction ?: "--"),
        "成交股數" to (stockDetail?.tradeVolume ?: "--"),
        "成交金額" to (stockDetail?.tradeValue ?: "--")
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
        "開盤價" to (stockDetail?.openingPrice ?: "--"),
        "收盤價" to (stockDetail?.closingPrice ?: "--"),
        "最高價" to (stockDetail?.highestPrice ?: "--"),
        "最低價" to (stockDetail?.lowestPrice ?: "--"),
        "漲跌價差" to (stockDetail?.change ?: "--"),
        "月平均價" to (stockAverage?.monthlyAveragePrice ?: "--"),
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
                    Text(
                        text = "${details[index].first}: ${details[index].second}",
                        fontSize = 12.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StockTitle(stockDetail: StockDetail?,
                       modifier: Modifier = Modifier) {
    Column {
        Text(
            text = stockDetail?.code ?: "--",
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 8.dp)
        )
        Text(
            text = stockDetail?.name ?: "----",
            fontSize = 20.sp,
            modifier = Modifier
                .padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StockInfoListPreview() {
    TWStockInsightTheme {
        StockInfoList(null, null, null)
    }
}