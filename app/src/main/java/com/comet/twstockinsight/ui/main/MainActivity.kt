package com.comet.twstockinsight.ui.main

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.comet.twstockinsight.R
import com.comet.twstockinsight.data.model.StockAverage
import com.comet.twstockinsight.data.model.StockBwi
import com.comet.twstockinsight.data.model.StockColorResult
import com.comet.twstockinsight.data.model.StockDetail
import com.comet.twstockinsight.ui.theme.TWStockInsightTheme
import com.comet.twstockinsight.ui.theme.Green
import com.comet.twstockinsight.ui.theme.Red
import com.comet.twstockinsight.util.Constants

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
    val lifecycleOwner = LocalLifecycleOwner.current

    val stockDetailList = mMainViewModel.stockDetailList.collectAsState()
    val stockAverageList = mMainViewModel.stockAverageList.collectAsState()
    val stockBwiList = mMainViewModel.stockBwiList.collectAsState()

    // load data only first time
    LaunchedEffect(lifecycleOwner) {
        // only update UI when activity is in started state
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            mMainViewModel.fetchAllConcurrently()
        }
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
            StockCard(
                stockDetailList?.get(item),
                stockAverageList?.get(item),
                onClickBwi = { stockCode, stockName ->
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
                text = selectedName.value ?: Constants.NO_DATA
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
        stringResource(R.string.transaction) to (stockDetail?.transaction ?: Constants.NO_DATA),
        stringResource(R.string.trade_volume) to (stockDetail?.tradeVolume ?: Constants.NO_DATA),
        stringResource(R.string.trade_value) to (stockDetail?.tradeValue ?: Constants.NO_DATA)
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
        stringResource(R.string.opening_price) to (stockDetail?.openingPrice ?: Constants.NO_DATA),
        stringResource(R.string.closeing_price) to (stockDetail?.closingPrice ?: Constants.NO_DATA),
        stringResource(R.string.highest_price) to (stockDetail?.highestPrice ?: Constants.NO_DATA),
        stringResource(R.string.lowest_price) to (stockDetail?.lowestPrice ?: Constants.NO_DATA),
        stringResource(R.string.price_change) to (stockDetail?.change ?: Constants.NO_DATA),
        stringResource(R.string.monthly_average_price) to (stockAverage?.monthlyAveragePrice ?: Constants.NO_DATA),
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
                    val colorResult = StockColors(stockDetail, stockAverage)
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
private fun StockColors(
    stockDetail: StockDetail?,
    stockAverage: StockAverage?
): StockColorResult  {
    val closingPriceDouble = stockDetail?.closingPrice?.toDoubleOrNull() ?: 0.0
    val monthlyAveragePriceDouble = stockAverage?.monthlyAveragePrice?.toDoubleOrNull() ?: 0.0
    val closePriceColor = when {
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
            text = stockDetail?.code ?: Constants.NO_DATA,
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 8.dp)
        )
        Text(
            text = stockDetail?.name ?: Constants.NO_NAME,
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