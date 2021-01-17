package com.example.triviacompose

import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.ConstraintSet
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.triviacompose.ui.answer.ShowAnswersList
import com.example.triviacompose.ui.category.DisplayCategories
import com.example.triviacompose.ui.common.CenterLoadingIndicator
import com.example.triviacompose.ui.common.onNextClicked
import com.example.triviacompose.ui.common.onPreviousClicked
import com.example.triviacompose.ui.error.ErrorView
import com.example.triviacompose.ui.question.questionCard
import com.example.triviacompose.ui.results.DisplayAnsweredQuestions
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalLayout
@ExperimentalCoroutinesApi
@Composable
fun HomeScreen(
    mainActivityViewModel: MainActivityViewModel
) {
    val state = mainActivityViewModel.state.collectAsState()

    when (state.value) {
        MainActivityViewModel.State.Idle -> {
            mainActivityViewModel.getCategories()
        }
        MainActivityViewModel.State.Loading -> CenterLoadingIndicator()
        is MainActivityViewModel.State.Success -> {
            GameScreen(mainActivityViewModel)
        }
        is MainActivityViewModel.State.Error -> ErrorView(retryAction = { mainActivityViewModel.getQuestions() })
        is MainActivityViewModel.State.Complete -> DisplayAnsweredQuestions(mainActivityViewModel)
        is MainActivityViewModel.State.SelectCategory -> DisplayCategories(mainActivityViewModel)
    }
}

@ExperimentalLayout
@ExperimentalCoroutinesApi
@Composable
fun GameScreen(
        mainActivityViewModel: MainActivityViewModel
) {
    WithConstraints { constraints
        val boxWidth = with(AmbientDensity.current) { constraints.maxWidth.toDp() }
        val constraints = if (minWidth < 600.dp) {
            decoupledConstraints(margin = 20.dp) // Portrait constraints
        } else {
            decoupledConstraints(margin = 32.dp) // Landscape constraints
        }
        ConstraintLayout(constraints) {
            questionCard(mainActivityViewModel = mainActivityViewModel)
            ShowAnswersList(mainActivityViewModel = mainActivityViewModel)
            onNextClicked(viewModel = mainActivityViewModel)
            onPreviousClicked(viewModel = mainActivityViewModel)
        }
    }
}

private fun decoupledConstraints(margin: Dp): ConstraintSet {
    return ConstraintSet {
        val questionCard = createRefFor("QuestionCard")
        val showAnswers = createRefFor("ShowAnswers")
        constrain(showAnswers) {
            top.linkTo(questionCard.bottom, margin = 0.dp)
        }
    }
}

