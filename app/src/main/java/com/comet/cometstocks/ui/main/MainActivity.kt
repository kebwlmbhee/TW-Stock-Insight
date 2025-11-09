@file:OptIn(ExperimentalMaterial3Api::class)

package com.comet.cometstocks.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.comet.cometstocks.ui.main.viewmodel.FakeMainViewModel
import com.comet.cometstocks.ui.main.viewmodel.MainViewModel
import com.comet.cometstocks.ui.theme.CometStocksTheme

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
            CometStocksTheme {
                MainScreenView().MainScreen(stockViewModel = mMainViewModel)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    CometStocksTheme {
        MainScreenView().MainScreen(FakeMainViewModel())
    }
}