package com.interview.flagchallenge.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.interview.flagchallenge.data.QuestionList
import com.interview.flagchallenge.common.utils.QuestionUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class QuestionViewModel : ViewModel() {

    private val _questions = MutableLiveData<QuestionList>()
    private val _timeLeft = MutableLiveData<Long>()
    private var countdownJob: Job? = null
    val counter: LiveData<QuestionList> get() = _questions
    val timeLeft: LiveData<Long> get() = _timeLeft

    /**
     * This method will get json object using util method from QuestionUtil
     */
    fun getQuestions(context: Context) {
        val jsonStringOutput = QuestionUtil.getJsonFromAssets(context, "questions.json")
        val questionObject = Gson().fromJson(jsonStringOutput, QuestionList::class.java)
        _questions.value = questionObject
    }

    /**
     * This method will start count down
     */
    fun startCountdown(enteredTime: String) {
        countdownJob?.cancel() /* Cancel any existing countdown */
        val units: List<String> = enteredTime.split(":") /* will break the string up into an array */
        val minutes = units[0].toInt() /* first element */
        val seconds = units[1].toInt() /* second element */
        val duration = 60 * minutes + seconds /* add up our values */
        countdownJob = viewModelScope.launch {
            var time = duration
            while (time > 0) {
                _timeLeft.postValue(time.toLong())
                delay(1000) // Wait for 1 second
                time--
            }
            _timeLeft.postValue(0)
        }
    }

    fun stopCountdown() {
        countdownJob?.cancel()
    }
}