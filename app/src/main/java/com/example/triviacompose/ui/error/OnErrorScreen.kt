package com.example.triviacompose.ui.error

import androidx.compose.foundation.ClickableText
import androidx.compose.foundation.layout.*
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp

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
