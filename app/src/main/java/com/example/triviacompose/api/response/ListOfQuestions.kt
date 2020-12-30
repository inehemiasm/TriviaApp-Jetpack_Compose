package com.example.triviacompose.api.response

data class ListOfQuestions(
    val response_code: Int,
    val results: List<TriviaQuestion>
)
