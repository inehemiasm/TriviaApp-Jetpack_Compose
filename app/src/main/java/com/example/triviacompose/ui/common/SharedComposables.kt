package com.example.triviacompose.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.example.triviacompose.MainActivityViewModel
import com.example.triviacompose.ui.typography
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@Composable
fun CustomText(answer: String) {
    Text(
        text = answer,
        modifier = Modifier
            .shadow(5.dp)
            .background(color = MaterialTheme.colors.onBackground,
                shape = RoundedCornerShape(5.dp))
            .padding(10.dp),
        overflow = TextOverflow.Ellipsis,
        style = TextStyle(fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.background),
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
            .clip(shape = RoundedCornerShape(16.dp)),
    ) {
        Box(modifier = Modifier
                .fillMaxHeight(.40f)
                .border(width = 8.dp, color = MaterialTheme.colors.onBackground,
                        shape = RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center) {
            Text(text = string,
                    Modifier.padding(15.dp),
                    textAlign = TextAlign.Center,
                    style = typography.h5
            ) }
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
