package com.example.tokopaerbe.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

object LiveDataTestUtil {
    @Throws(InterruptedException::class)
    fun <T> getValue(liveData: LiveData<T>): T {
        val data = arrayOfNulls<Any>(1)
        val latch = CountDownLatch(1)

        val observer = Observer<T> {
            data[0] = it
            latch.countDown()
        }

        liveData.observeForever(observer)

        latch.await(2, TimeUnit.SECONDS)

        @Suppress("UNCHECKED_CAST")
        return data[0] as T
    }
}
