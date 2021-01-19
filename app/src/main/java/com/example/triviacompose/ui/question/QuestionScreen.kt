package com.example.triviacompose.ui.question

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import com.example.triviacompose.MainActivityViewModel
import com.example.triviacompose.ui.answer.ShowAnswersList
import com.example.triviacompose.ui.common.CenteredColumn
import com.example.triviacompose.ui.main.CenteredTitle
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Composable
fun questionCard(
        mainActivityViewModel: MainActivityViewModel
) {
    val currentQuestionText = mainActivityViewModel.currentQuestionText.collectAsState()
    Card(shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                    .padding(10.dp)
                    .shadow(15.dp)
                    .layoutId("QuestionCard")) {
        CenteredColumn(string = currentQuestionText.value)
    }
}

@ExperimentalLayout
@ExperimentalCoroutinesApi
@Composable
fun TimerView(mainActivityViewModel: MainActivityViewModel) {
    val currentQTimer = mainActivityViewModel.timer.collectAsState()
    var barColor = Color.Green
    when {
        currentQTimer.value > 0 -> {
            if (currentQTimer.value < .50) barColor = Color.Red
            ShowAnswersList(mainActivityViewModel = mainActivityViewModel)
            Box(modifier = Modifier.layoutId("timer").height(20.dp).fillMaxWidth(),
                contentAlignment = Alignment.Center) {
                LinearProgressIndicator(progress = currentQTimer.value,
                    color = barColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp))
            }
        } else -> Box(modifier = Modifier.layoutId("timer")) {
        CenteredTitle("Time is up!!")
        }
    }
}
