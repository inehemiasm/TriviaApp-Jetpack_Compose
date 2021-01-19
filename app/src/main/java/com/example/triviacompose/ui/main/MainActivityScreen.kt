package com.example.triviacompose.ui.main

import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.material.TopAppBar
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Icon
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.triviacompose.HomeScreen
import com.example.triviacompose.MainActivityViewModel
import com.example.triviacompose.api.ApiRequest
import com.example.triviacompose.ui.common.CustomText
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalLayout
@ExperimentalCoroutinesApi
@Composable
fun TriviaAppScaffold(
    networkClient: ApiRequest
) {
    val mainActivityViewModel: MainActivityViewModel = viewModel(null, object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            MainActivityViewModel(networkClient) as T
    })
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(title = { Box(modifier = Modifier.fillMaxWidth(.85f)) {
                CenteredTitle ("Trivia App")} },
                navigationIcon = {
                    Icon(
                        Icons.Filled.Menu,
                        tint = MaterialTheme.colors.onBackground,
                        modifier = Modifier
                            .padding(10.dp)
                            .clickable(onClick = {
                                scaffoldState.drawerState.open() // or toggle
                            })
                    )
                })
        },
        drawerShape = RoundedCornerShape(topRight = 10.dp, bottomRight = 10.dp),
        drawerContent = {
            DisplayScafoldOptions(mainActivityViewModel, scaffoldState)
        },
        bodyContent = {
            HomeScreen(mainActivityViewModel)
        }
    )
}

@Composable
fun CenteredTitle(text: String) {
    Text(text = text,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        style = TextStyle(fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Cursive)
    )
}

@ExperimentalCoroutinesApi
@Composable
fun DisplayScafoldOptions(mainActivityViewModel: MainActivityViewModel, scaffoldState: ScaffoldState) {
    val listOfOptions = listOf("Select Category", "Reset Game", "Settings", "Quit")
    CenteredTitle("Menu")
    Divider(color = MaterialTheme.colors.onBackground, thickness = 5.dp,
    modifier = Modifier.padding(10.dp)
        .shadow(5.dp))
    LazyColumn(modifier = Modifier.padding(10.dp)
    ) {
        items(items = listOfOptions,
            itemContent = { option ->
                Column(modifier = Modifier
                    .padding(5.dp)
                    .clickable(onClick = {
                        scaffoldState.drawerState.close()
                        with(mainActivityViewModel) { onItemClicked(option) }
                    })
                ) {
                    CustomText(answer = option)
                }
            })
    }
}
