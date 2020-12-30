package com.example.triviacompose

import com.example.triviacompose.api.RetrofitInstance

class Injector {
    private val retrofitInstance = RetrofitInstance()
    val networkClient by lazy {
        retrofitInstance.retrofit
    }
}
