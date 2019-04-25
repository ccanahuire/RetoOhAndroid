package com.ccanahuire.retooh.ui.components.watcher

import android.text.Editable
import android.text.TextWatcher

class OnReachMaxCharacterTextWatcher(
    private val maxCharacters: Int,
    private val onReachMaxCharacterListener: () -> Unit
) :
    TextWatcher {

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (s.length >= maxCharacters) {
            onReachMaxCharacterListener()
        }
    }

    override fun afterTextChanged(s: Editable) {
    }
}