package com.example.triviacompose

import androidx.compose.foundation.ClickableText
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triviacompose.ui.typography
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber

@ExperimentalLayout
@ExperimentalCoroutinesApi
@Composable
fun HomeScreen(
    mainActivityViewModel: MainActivityViewModel
) {

    val state = mainActivityViewModel.state.collectAsState()
    val currentIndex = mainActivityViewModel.questionIndex.collectAsState()

    when (state.value) {
        MainActivityViewModel.State.Idle -> {
            mainActivityViewModel.getCategories()
            mainActivityViewModel.getQuestions()
        }
        MainActivityViewModel.State.Loading -> CenterLoadingIndicator()
        is MainActivityViewModel.State.Success -> {
            DisplayCurrentQuestion(mainActivityViewModel)
        }
        is MainActivityViewModel.State.Error -> ErrorView(retryAction = { mainActivityViewModel.getQuestions() })
    }

}

@Composable
fun ErrorView(retryAction: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(all = 2.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Snackbar(text = { Text(text = "Error loading data") }, action = {
            ClickableText(text = AnnotatedString("Retry"), onClick = {
                retryAction()
            })
        })
    }
}

@ExperimentalCoroutinesApi
@Composable
fun onNextClicked(viewModel: MainActivityViewModel) {
    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(all = 15.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        Button(onClick = { viewModel.onNextClicked() },
        modifier = Modifier
            .padding(20.dp)) {
            Text(text = "Next",
            modifier = Modifier.width(70.dp)
                .align(Alignment.CenterVertically))
        }
    }
}

@ExperimentalCoroutinesApi
@Composable
fun onPreviousClicked(viewModel: MainActivityViewModel) {
    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(all = 15.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.Start
    ) {
        Button(onClick = { viewModel.onPreviousClicked() },
            modifier = Modifier
                .padding(20.dp)
        ) {
            Text(text = "Previous")
        }
    }
}

@ExperimentalCoroutinesApi
fun onAnswerClicked(
    viewModel: MainActivityViewModel,
    answerClicked: String
) {
    viewModel.onAnswerSelected(answerClicked)
}

@ExperimentalLayout
@ExperimentalCoroutinesApi
@Composable
fun DisplayCurrentQuestion(
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

@Composable
fun CenterLoadingIndicator() {
    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(modifier = Modifier
            .align(Alignment.CenterHorizontally)
        )
    }
}
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
fun questionCard(
    mainActivityViewModel: MainActivityViewModel
) {
    //val questionText = mainActivityViewModel.listOfQuestions.value[mainActivityViewModel.questionIndex.value].question
    val currentQuestionText = mainActivityViewModel.currentQuestionText.collectAsState()
    Card(shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(10.dp)
            .shadow(15.dp)
            .layoutId("QuestionCard")) {
        CenteredColumn(string = currentQuestionText.value)
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

@ExperimentalCoroutinesApi
@Composable
fun AnswerListText(
    answer: String,
    mainActivityViewModel: MainActivityViewModel
) {
    Timber.e("Created Answer $answer")
    Text(
        text = answer,
        modifier = Modifier
            .background(
                Brush.verticalGradient(
                    listOf(Color.Transparent, Color.LightGray, Color.Transparent),
                    20f,  // TODO: set start
                    100f  // TODO: set end
                )
            ).padding(10.dp)
            .clickable(onClick = {
                onAnswerClicked(
                    mainActivityViewModel,
                    answer
                )
            }),
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        style = TextStyle(fontWeight = FontWeight.Bold),
        fontSize = 24.sp
    )
}

@Composable
fun CenteredColumn(string: String) {
    Column(modifier = Modifier
        .layoutId("QuestionCard")
        .padding(10.dp)
        .fillMaxWidth()
        .wrapContentSize(Alignment.Center)
        .clickable(onClick = {  } )
        .clip(shape = RoundedCornerShape(16.dp)),
    ) {
        Box(modifier = Modifier
            .fillMaxHeight(.40f)
            .border(width = 8.dp, color = MaterialTheme.colors.onBackground,
                shape = RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center) {
            //Text(text = string, modifier = Modifier)
            Text(text = string,
                Modifier.padding(15.dp),
                textAlign = TextAlign.Center,
                style = typography.h5
            ) }
    }
}

