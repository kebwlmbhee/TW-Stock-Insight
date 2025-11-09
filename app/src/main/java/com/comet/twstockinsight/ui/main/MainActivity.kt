@file:OptIn(ExperimentalMaterial3Api::class)

package com.comet.twstockinsight.ui.main

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
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
import com.comet.twstockinsight.ui.main.MainActivity.Companion.NO_DATA
import com.comet.twstockinsight.ui.main.MainActivity.Companion.NO_NAME
import com.comet.twstockinsight.ui.theme.Green
import com.comet.twstockinsight.ui.theme.Red
import com.comet.twstockinsight.ui.theme.TWStockInsightTheme

class MainActivity : ComponentActivity() {

    companion object {
        private val TAG = MainActivity::class.qualifiedName
        const val NO_DATA = "--"
        const val NO_NAME = "----"
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

    var expanded by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val currentSortOrder by mMainViewModel.currentSortOrder.collectAsState()

    // load data only first time
    LaunchedEffect(lifecycleOwner) {
        // only update UI when activity is in started state
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            mMainViewModel.fetchAllConcurrently()
        }
    }

    Scaffold(modifier = Modifier
        .nestedScroll(scrollBehavior.nestedScrollConnection)
        .fillMaxSize(),
        // https://developer.android.com/develop/ui/compose/components/app-bars?hl=zh-tw#center
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("") },
                actions = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Sort"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        StockInfoList(
            stockDetailList.value,
            stockAverageList.value,
            stockBwiList.value,
            modifier = Modifier.padding(innerPadding)
        )
    }

    if (expanded) {
        ModalBottomSheet(
            onDismissRequest = { expanded = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                TextButton(colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Unspecified,
                    contentColor = Color.Unspecified),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    onClick = { mMainViewModel.sortStockListByCode(SortOrder.DESC) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(stringResource(R.string.sort_desc_by_stock_code))
                        Spacer(modifier = Modifier.weight(1f))
                        if (currentSortOrder == SortOrder.DESC) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_check),
                                contentDescription = "Selected"
                            )
                        }
                    }
                }
                TextButton(colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Unspecified,
                    contentColor = Color.Unspecified),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    onClick = { mMainViewModel.sortStockListByCode(SortOrder.ASC) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(stringResource(R.string.sort_asc_by_stock_code))
                        Spacer(modifier = Modifier.weight(1f))
                        if (currentSortOrder == SortOrder.ASC) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_check),
                                contentDescription = "Selected",
                            )
                        }
                    }
                }
            }
        }
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
        stringResource(R.string.transaction) to (stockDetail?.transaction ?: NO_DATA),
        stringResource(R.string.trade_volume) to (stockDetail?.tradeVolume ?: NO_DATA),
        stringResource(R.string.trade_value) to (stockDetail?.tradeValue ?: NO_DATA)
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
        stringResource(R.string.opening_price) to (stockDetail?.openingPrice ?: NO_DATA),
        stringResource(R.string.closeing_price) to (stockDetail?.closingPrice ?: NO_DATA),
        stringResource(R.string.highest_price) to (stockDetail?.highestPrice ?: NO_DATA),
        stringResource(R.string.lowest_price) to (stockDetail?.lowestPrice ?: NO_DATA),
        stringResource(R.string.price_change) to (stockDetail?.change ?: NO_DATA),
        stringResource(R.string.monthly_average_price) to (stockAverage?.monthlyAveragePrice ?: NO_DATA),
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
            text = stockDetail?.code ?: NO_DATA,
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 8.dp)
        )
        Text(
            text = stockDetail?.name ?: NO_NAME,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StockInfoListPreview() {
    val mainViewModel = MainViewModel().setFakeData()
    TWStockInsightTheme {
        MainScreen(mMainViewModel = mainViewModel)
    }
}