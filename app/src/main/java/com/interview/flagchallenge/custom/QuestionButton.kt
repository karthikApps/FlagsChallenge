package com.interview.flagchallenge.custom

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import com.interview.flagchallenge.R

class QuestionButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

    var isCorrectAnswer: Boolean = true

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.CustomButton, 0, 0)
            isCorrectAnswer = typedArray.getBoolean(R.styleable.CustomButton_isCorrectAnswer, false)
            typedArray.recycle()
        }
        updateButtonState()
    }

    private fun updateButtonState() {
        isEnabled = isCorrectAnswer
    }
}