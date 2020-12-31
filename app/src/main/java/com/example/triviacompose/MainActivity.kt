package com.example.triviacompose

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Icon
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = Color.White,
        topBar = {
            TopAppBar(title = { CenteredTitle() },
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
            DisplayCategories(mainActivityViewModel, scaffoldState)
        },
        bodyContent = {
            HomeScreen(mainActivityViewModel)
        }
    )
}
@Composable
fun CenteredTitle() {
    Text(text = "Trivia App",
        modifier = Modifier.fillMaxWidth(.85f),
        textAlign = TextAlign.Center,
        style = TextStyle(fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Cursive)
    )
}
@ExperimentalCoroutinesApi
@Composable
fun DisplayCategories(mainActivityViewModel: MainActivityViewModel, scaffoldState: ScaffoldState) {
    val listOfCategoriesState = mainActivityViewModel.listOfCategories.collectAsState()
    LazyColumn(modifier = Modifier.padding(10.dp)
    ) {
        items(items = listOfCategoriesState.value,
            itemContent = { triviaCategory ->
                Column(modifier = Modifier
                    .padding(5.dp)
                    .clickable(onClick = {
                        scaffoldState.drawerState.close()
                    with(mainActivityViewModel) { onCategorySelected(triviaCategory.id) }
                })) {
                    CustomText(answer = triviaCategory.name)
                }
            })
    }
}


