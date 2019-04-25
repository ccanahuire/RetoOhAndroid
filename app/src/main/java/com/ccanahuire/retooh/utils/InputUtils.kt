package com.ccanahuire.retooh.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

abstract class InputUtils {
    companion object {
        fun hideSoftKeyboard(context: Context?, view: View) {
            val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}