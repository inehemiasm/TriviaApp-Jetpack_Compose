package com.example.triviacompose.ui.results

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triviacompose.MainActivityViewModel
import com.example.triviacompose.model.AnsweredQuestion
import com.example.triviacompose.ui.common.CenteredColumn
import com.example.triviacompose.ui.common.CustomText
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Composable
fun DisplayAnsweredQuestions(mainActivityViewModel: MainActivityViewModel) {
    val listOfQuestionsState = mainActivityViewModel.listOfAnsweredQuestions.collectAsState()
    Box {
        val resetGame = "Reset Game"
        LazyColumn(modifier = Modifier.padding(10.dp)
        ) {
            items(items = listOfQuestionsState.value.values + resetGame,
                    itemContent = { item ->
                        if (item is String) {
                            Column(modifier = Modifier.clickable(onClick = { mainActivityViewModel.onItemClicked(item) })) {
                                CenteredColumn(string = item)
                            }
                        } else if (item is AnsweredQuestion) {
                            val selectedAnswer = item.selectedAnswer
                            var correctAnswer = item.correctAnswer
                            Column(modifier = Modifier
                                    .shadow(10.dp)
                                    .padding(10.dp)) {
                                CustomText(answer = item.question)
                                if (correctAnswer == selectedAnswer) {
                                    Text(text = correctAnswer, style = TextStyle(
                                        fontWeight = FontWeight.Bold, color = MaterialTheme.colors.primary), fontSize = 18.sp, modifier = Modifier.padding(5.dp)
                                    )
                                } else {
                                    val selected = "Selected: $selectedAnswer"
                                    correctAnswer = "Expected: $correctAnswer"
                                    Text(text = selected, style = TextStyle(
                                        fontWeight = FontWeight.Bold, color = Color.Red), fontSize = 18.sp, modifier = Modifier.padding(5.dp)
                                    )
                                    Text(text = correctAnswer, style = TextStyle(
                                        fontWeight = FontWeight.Bold, color = MaterialTheme.colors.primary), fontSize = 18.sp, modifier = Modifier.padding(5.dp)
                                    )
                                }
                            }
                        }
                    })
        }
    }
}
