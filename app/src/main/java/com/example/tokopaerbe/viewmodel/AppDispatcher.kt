package com.example.tokopaerbe.viewmodel

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.junit.rules.TestWatcher
import org.junit.runner.Description

//class AppDispatcher(
//    val testDispatcher: TestDispatcher = Standa(),
//) : TestWatcher() {
//    override fun starting(description: Description) {
//        Dispatchers.setMain(testDispatcher)
//    }
//
//    override fun finished(description: Description) {
//        Dispatchers.resetMain()
//    }
//}