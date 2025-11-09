package com.comet.cometstocks.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.comet.cometstocks.R
import com.comet.cometstocks.ui.main.viewmodel.FakeMainViewModel
import com.comet.cometstocks.ui.main.viewmodel.SortOrder
import com.comet.cometstocks.ui.main.viewmodel.StockViewModel
import com.comet.cometstocks.ui.theme.CometStocksTheme

class MainScreenView {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreen(stockViewModel: StockViewModel) {
        val lifecycleOwner = LocalLifecycleOwner.current

        val stockDetailList = stockViewModel.stockDetailList.collectAsState()
        val stockAverageList = stockViewModel.stockAverageList.collectAsState()
        val stockBwiList = stockViewModel.stockBwiList.collectAsState()

        var expanded by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

        val currentSortOrder by stockViewModel.currentSortOrder.collectAsState()

        // load data only first time
        LaunchedEffect(lifecycleOwner) {
            // only update UI when activity is in started state
            lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                stockViewModel.loadSortOrder()
                stockViewModel.fetchAllConcurrently()
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
            StockInfoListView().StockInfoList(
                stockDetailList.value,
                stockAverageList.value,
                stockBwiList.value,
                modifier = Modifier.padding(innerPadding)
            )
        }

        if (expanded) {
            ModalBottomSheet(
                onDismissRequest = { expanded = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    TextButton(colors = ButtonDefaults.textButtonColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        onClick = { stockViewModel.sortStockListByCode(SortOrder.DESC) }) {
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
                    TextButton(colors = ButtonDefaults.textButtonColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        onClick = { stockViewModel.sortStockListByCode(SortOrder.ASC) }) {
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
}

@Preview(showBackground = true)
@Composable
fun MainScreenViewPreview() {
    CometStocksTheme {
        MainScreenView().MainScreen(FakeMainViewModel())
    }
}
