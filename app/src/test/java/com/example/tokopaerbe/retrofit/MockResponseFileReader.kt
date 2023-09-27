package com.example.tokopaerbe.retrofit

import java.io.InputStreamReader

class MockResponseFileReader(path: String) {

    val content: String

    init {
        val reader = InputStreamReader(javaClass.classLoader?.getResourceAsStream(path))
        content = reader.readText()
        reader.close()
    }
}