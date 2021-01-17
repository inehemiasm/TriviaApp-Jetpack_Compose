package com.example.triviacompose.ui.question

import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.unit.dp
import com.example.triviacompose.*
import com.example.triviacompose.ui.answer.ShowAnswersList
import com.example.triviacompose.ui.common.CenteredColumn
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
