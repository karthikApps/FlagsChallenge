package com.interview.flagchallenge.common.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.text.InputType
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import android.widget.TextView


/**
 * A custom EditText (actually derived from TextView) to input time in 24h format.
 *
 * Features:
 * - It always shows the currently set time, so it's never empty.
 * - Both virtual and physical keyboards can be used.
 * - The current digit is highlighted; when a number on the keyboard is pressed, the digit is replaced.
 * - Back key moves the cursor backward.
 * - Space key moves the cursor forward.
 *
 */
@SuppressLint("AppCompatCustomView")
class TimeEditText @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) :
    TextView(context!!, attrs, defStyle) {
    private val digits = IntArray(4)
    private var currentPosition = POSITION_NONE
    private var mImeOptions = 0

    init {
        isFocusableInTouchMode = true

        if (attrs != null && !isInEditMode) {
            mImeOptions = attrs.getAttributeIntValue(
                "http://schemas.android.com/apk/res/android",
                "imeOptions",
                0
            )
        }

        updateText()
    }

    var hour: Int
        /**
         * @return the current hour (from 0 to 23)
         */
        get() = digits[0] * 10 + digits[1]
        /**
         * Set the current hour
         * @param hour hour (from 0 to 23)
         */
        set(hour) {
            var hour = hour
            hour = hour % 24
            digits[0] = hour / 10
            digits[1] = hour % 10
            updateText()
        }

    var minutes: Int
        /**
         * @return the current minute
         */
        get() = digits[2] * 10 + digits[3]
        /**
         * Set the current minute
         * @param min minutes (from 0 to 59)
         */
        set(min) {
            var min = min
            min = min % 60
            digits[2] = min / 10
            digits[3] = min % 10
            updateText()
        }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        // hide cursor if not focused
        currentPosition = if (focused) 0 else POSITION_NONE
        updateText()
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
    }

    private fun updateText() {
        val bold = if (currentPosition > 1) currentPosition + 1 else currentPosition
        val color = textColors.defaultColor
        val text: Spannable = SpannableString(
            String.format(
                "%02d:%02d",
                hour,
                minutes
            )
        )
        if (bold >= 0) {
            text.setSpan(
                ForegroundColorSpan(color and 0xFFFFFF or -0x60000000),
                0,
                5,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            text.setSpan(
                StyleSpan(Typeface.BOLD),
                bold,
                bold + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            text.setSpan(
                ForegroundColorSpan(Color.BLACK),
                bold,
                bold + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            text.setSpan(
                BackgroundColorSpan(0x40808080),
                bold,
                bold + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        setText(text)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            requestFocusFromTouch()
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(this, 0)
            if (currentPosition == POSITION_NONE) {
                currentPosition = 0
                updateText()
            }
        }
        return true
    }

    private fun onKeyEvent(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null && event.action != KeyEvent.ACTION_DOWN) return false

        if (keyCode == KeyEvent.KEYCODE_DEL) {
            // moves cursor backward
            currentPosition = if (currentPosition >= 0) (currentPosition + 3) % 4 else 3
            updateText()
            return true
        }

        if (keyCode == KeyEvent.KEYCODE_SPACE) {
            // moves cursor forward
            currentPosition = (currentPosition + 1) % 4
            updateText()
            return true
        }

        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            val v = focusSearch(FOCUS_DOWN)
            var next = v != null
            if (next) {
                next = v!!.requestFocus(FOCUS_DOWN)
            }
            if (!next) {
                hideKeyboard()
                currentPosition = POSITION_NONE
                updateText()
            }
            return true
        }

        val c = event!!.unicodeChar.toChar()
        if (c >= '0' && c <= '9') {
            currentPosition = if (currentPosition == POSITION_NONE) 0 else currentPosition
            val n = c.code - '0'.code
            var valid = false

            when (currentPosition) {
                0 -> valid = n <= 2
                1 -> valid = digits[0] < 2 || n <= 3
                2 -> valid = n < 6
                3 -> valid = true
            }
            if (valid) {
                if (currentPosition == 0 && n == 2 && digits[1] > 3) { // clip to 23 hours max
                    digits[1] = 3
                }

                digits[currentPosition] = n
                currentPosition =
                    if (currentPosition < 3) currentPosition + 1 else POSITION_NONE // if it is the last digit, hide cursor
                updateText()
            }

            return true
        }

        return false
    }

    private fun hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        // events from physical keyboard
        return onKeyEvent(keyCode, event)
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        // manage events from the virtual keyboard
        outAttrs.actionLabel = null
        outAttrs.label = "time"
        outAttrs.inputType = InputType.TYPE_CLASS_NUMBER
        outAttrs.imeOptions = mImeOptions or EditorInfo.IME_FLAG_NO_EXTRACT_UI

        if ((outAttrs.imeOptions and EditorInfo.IME_MASK_ACTION) == EditorInfo.IME_ACTION_UNSPECIFIED) {
            if (focusSearch(FOCUS_DOWN) != null) {
                outAttrs.imeOptions = outAttrs.imeOptions or EditorInfo.IME_ACTION_NEXT
            } else {
                outAttrs.imeOptions = outAttrs.imeOptions or EditorInfo.IME_ACTION_DONE
            }
        }

        return object : BaseInputConnection(this, false) {
            override fun performEditorAction(actionCode: Int): Boolean {
                if (actionCode == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard()
                    currentPosition = POSITION_NONE
                    updateText()
                } else if (actionCode == EditorInfo.IME_ACTION_NEXT) {
                    val v = focusSearch(FOCUS_DOWN)
                    v?.requestFocus(FOCUS_DOWN)
                }
                return true
            }

            override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
                onKeyEvent(KeyEvent.KEYCODE_DEL, null)
                return true
            }

            override fun sendKeyEvent(event: KeyEvent): Boolean {
                onKeyEvent(event.keyCode, event)
                return true
            }
        }
    }

    companion object {
        private const val POSITION_NONE = -1
    }
}