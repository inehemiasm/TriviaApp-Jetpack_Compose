package com.example.triviacompose.api.response

import com.example.triviacompose.model.TriviaQuestion

data class ListOfQuestions(
    val response_code: Int,
    val results: List<TriviaQuestion>
)
