package com.example.triviacompose

import android.app.Application
import timber.log.Timber

class TriviaApplication : Application() {
    lateinit var injector: Injector
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        injector = Injector()
    }
}