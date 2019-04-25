package com.ccanahuire.retooh.utils

abstract class FormatUtils {
    companion object {
        fun formatPhoneNumber(phoneNumber: String): String {
            val phoneDigits = digitsOnly(phoneNumber)
            if (phoneDigits.length in 4..6) {
                return "${phoneDigits.substring(0, 3)} ${phoneDigits.substring(3)}"
            } else if (phoneDigits.length > 6) {
                return "${phoneDigits.substring(0, 3)} ${phoneDigits.substring(3, 6)} ${phoneDigits.substring(6)}"
            }

            return phoneDigits
        }

        fun digitsOnly(string: String): String {
            return string.replace("\\D+".toRegex(), "")
        }
    }
}