package com.ccanahuire.retooh.ui

import android.os.Bundle
import androidx.annotation.IdRes
import com.ccanahuire.retooh.R
import com.ccanahuire.retooh.ui.loading.LoadingFragment
import com.ccanahuire.retooh.ui.register.RegisterNameFragment
import com.ccanahuire.retooh.utils.TransitionHelper

class RegistrationActivity : NavigationHostActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        if (savedInstanceState == null) {
            navigateToRegisterName()
        }
    }

    @IdRes
    override fun containerId(): Int {
        return R.id.container
    }

    private fun navigateToRegisterName() {
        val registerNameFragment = RegisterNameFragment()
        TransitionHelper.setDefaultTransition(registerNameFragment)
        navigateTo(registerNameFragment, false)
    }

    override fun onBackPressed() {
        // Avoid cancelling loading fragment.
        if (lastNavigatedFragment is LoadingFragment) {
            return
        }
        super.onBackPressed()
    }
}
