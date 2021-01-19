package com.example.triviacompose.ui.category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.triviacompose.MainActivityViewModel
import com.example.triviacompose.ui.common.CustomText
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Composable
fun DisplayCategories(mainActivityViewModel: MainActivityViewModel) {
    val listOfCategoriesState = mainActivityViewModel.listOfCategories.collectAsState()
    LazyColumn(modifier = Modifier.padding(10.dp)
    ) {
        items(items = listOfCategoriesState.value,
                itemContent = { triviaCategory ->
                    Column(modifier = Modifier
                            .padding(5.dp)
                            .clickable(onClick = {
                                //scaffoldState.drawerState.close()
                                with(mainActivityViewModel) { onItemClicked(triviaCategory.id) }
                            })) {
                        CustomText(answer = triviaCategory.name)
                    }
                })
    }
}
