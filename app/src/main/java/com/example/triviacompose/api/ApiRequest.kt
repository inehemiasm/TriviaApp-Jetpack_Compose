package com.example.triviacompose.api

import com.example.triviacompose.api.response.Categories
import com.example.triviacompose.api.response.ListOfQuestions
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiRequest {
    @GET("/api_category.php")
    fun getCategory(): Call<Categories>

    @GET("/api.php")
    fun getQuestions(
        @Query("amount") amount: Int,
        @Query("category") category: Int
    ): Call<ListOfQuestions>
}
