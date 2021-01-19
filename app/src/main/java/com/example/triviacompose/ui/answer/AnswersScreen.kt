package com.example.triviacompose.ui.answer

import com.example.triviacompose.ui.common.CustomText
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max
import com.example.triviacompose.MainActivityViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import androidx.compose.foundation.layout.Column


@ExperimentalLayout
@Composable
@ExperimentalCoroutinesApi
fun ShowAnswersList(
        mainActivityViewModel: MainActivityViewModel
) {
    val mapOfAnswers = mainActivityViewModel.answersMap.collectAsState()
    Box(modifier = Modifier.padding(10.dp)
        .layoutId("ShowAnswers")) {
        CustomFlowRow(
            verticalGap = 8.dp,
            horizontalGap = 8.dp,
            alignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp).fillMaxWidth()
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
    Column(modifier = Modifier.clickable(onClick = {
        with(mainActivityViewModel) { onItemClicked(answer) } })) {
        CustomText(answer = answer)
    }
}

@Composable
fun CustomFlowRow(
    modifier: Modifier = Modifier,
    alignment: Alignment.Horizontal = Alignment.Start,
    verticalGap: Dp = 0.dp,
    horizontalGap: Dp = 0.dp,
    content: @Composable () -> Unit
) = Layout(content, modifier) { measurables, constraints ->
    val hGapPx = horizontalGap.toIntPx()
    val vGapPx = verticalGap.toIntPx()

    val rows = mutableListOf<MeasuredRow>()
    val itemConstraints = constraints.copy(minWidth = 0)
    for (measurable in measurables) {
        val lastRow = rows.lastOrNull()
        val placeable = measurable.measure(itemConstraints)

        if (lastRow != null && lastRow.width + hGapPx + placeable.width <= constraints.maxWidth) {
            lastRow.items.add(placeable)
            lastRow.width += hGapPx + placeable.width
            lastRow.height = max(lastRow.height, placeable.height)
        } else {
            val nextRow = MeasuredRow(
                items = mutableListOf(placeable),
                width = placeable.width,
                height = placeable.height
            )
            rows.add(nextRow)
        }
    }

    val width = rows.maxOfOrNull { row -> row.width } ?: 0
    val height = rows.sumBy { row -> row.height } + max(vGapPx.times(rows.size - 1), 0)

    val coercedWidth = width.coerceIn(constraints.minWidth, constraints.maxWidth)
    val coercedHeight = height.coerceIn(constraints.minHeight, constraints.maxHeight)

    layout(coercedWidth, coercedHeight) {
        var y = 0
        for (row in rows) {
            var x = when(alignment) {
                Alignment.Start -> 0
                Alignment.CenterHorizontally -> (coercedWidth - row.width) / 2
                Alignment.End -> coercedWidth - row.width
                else -> throw Exception("unsupported alignment")
            }

            for (item in row.items) {
                item.place(x, y)
                x += item.width + hGapPx
            }

            y += row.height + vGapPx
        }
    }
}

private data class MeasuredRow(
    val items: MutableList<Placeable>,
    var width: Int,
    var height: Int
)
