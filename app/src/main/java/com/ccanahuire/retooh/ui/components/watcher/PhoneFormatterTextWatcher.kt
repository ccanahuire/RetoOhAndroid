package com.ccanahuire.retooh.ui.components.watcher

import android.text.Editable
import android.text.TextWatcher
import com.ccanahuire.retooh.utils.FormatUtils

class PhoneFormatterTextWatcher : TextWatcher {
    private var isDeletingCharacters = false
    private var previousLength = 0
    private var isChanging = false

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        previousLength = s.length
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        isDeletingCharacters = s.length < previousLength
    }

    override fun afterTextChanged(s: Editable) {
        if (!isDeletingCharacters && !isChanging) {
            isChanging = true
            val formattedPhone = FormatUtils.formatPhoneNumber(s.toString())
            s.clear()
            s.append(formattedPhone)
            isChanging = false
        }
    }
}