package com.example.triviacompose

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Icon
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.triviacompose.api.ApiRequest
import com.example.triviacompose.ui.TriviaComposeTheme
import com.example.triviacompose.ui.typography
import kotlinx.coroutines.ExperimentalCoroutinesApi

class MainActivity : AppCompatActivity() {

    @ExperimentalLayout
    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val injector = (application as TriviaApplication).injector
        setContent {
            TriviaComposeTheme {
                // A surface container using the 'background' color from the theme
                TriviaAppScaffold(
                    networkClient = injector.networkClient
                )
            }
        }
    }
}

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
    val listOfCategories = mainActivityViewModel.listOfCategories.collectAsState()
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = Color.White,
        topBar = {
            TopAppBar(title = { centeredTitle() },
                navigationIcon = {
                    Icon(
                        Icons.Default.Menu,
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
            listOfCategories.value.forEach {
                Text(text = it.name)
            }
        },
        bodyContent = {
                HomeScreen(mainActivityViewModel)
        }
    )
}
@Composable
fun centeredTitle() {
    Column(modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth()
        .wrapContentSize(Alignment.Center)
        .clickable(onClick = { } )
        .clip(shape = RoundedCornerShape(16.dp)),
    ) {
        Box(modifier = Modifier
            .preferredSize(500.dp)
            .border(width = 5.dp, color = Gray, shape = RoundedCornerShape(5.dp)),
            contentAlignment = Alignment.Center) {
            Text(
                "Trivia App",
                Modifier.padding(5.dp),
                textAlign = TextAlign.Center,
                style = typography.h6
            )
        }
    }
}
