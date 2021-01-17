package com.example.triviacompose.ui.answer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.SizeMode
import com.example.triviacompose.ui.common.CustomText
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.foundation.layout.MainAxisAlignment
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import com.example.triviacompose.MainActivityViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalLayout
@Composable
@ExperimentalCoroutinesApi
fun ShowAnswersList(
        mainActivityViewModel: MainActivityViewModel
) {
    val mapOfAnswers = mainActivityViewModel.answersMap.collectAsState()
    Box(modifier = Modifier.padding(10.dp)
            .layoutId("ShowAnswers")) {
        FlowRow(
                mainAxisAlignment = MainAxisAlignment.Center,
                mainAxisSize = SizeMode.Expand,
                crossAxisSpacing = 12.dp,
                mainAxisSpacing = 15.dp
        ) {
            mapOfAnswers.value.forEach {
                AnswerListText(it.key, mainActivityViewModel)
            }
        }
    }
}

@ExperimentalCoroutinesApi
@Composable
fun AnswerListText(
        answer: String,
        mainActivityViewModel: MainActivityViewModel
) {
    Column(modifier = Modifier.clickable(onClick = { with(mainActivityViewModel) { onItemClicked(answer) } })) {
        CustomText(answer = answer)
    }
}