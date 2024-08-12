package com.interview.flagchallenge.common.utils

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Created by Karthik on 11/08/2024.
 */
object QuestionUtil {

    fun getJsonFromAssets(context: Context, fileName: String): String? {
        return try {
            val inputStream = context.assets.open(fileName)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            bufferedReader.use { it.readText() }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}