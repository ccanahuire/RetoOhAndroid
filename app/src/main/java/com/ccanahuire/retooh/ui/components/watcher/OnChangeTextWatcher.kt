package com.ccanahuire.retooh.ui.components.watcher

import android.text.Editable
import android.text.TextWatcher

class OnChangeTextWatcher(private val onChangeListener: () -> Unit) : TextWatcher {

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        onChangeListener()
    }

    override fun afterTextChanged(s: Editable) {
    }
}