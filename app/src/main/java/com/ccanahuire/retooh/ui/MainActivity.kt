package com.ccanahuire.retooh.ui

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.ccanahuire.retooh.R
import com.ccanahuire.retooh.ui.loading.LoadingFragment
import com.ccanahuire.retooh.ui.welcome.WelcomeFragment
import com.ccanahuire.retooh.utils.TransitionHelper

class MainActivity : NavigationHostActivity() {

    @IdRes
    override fun containerId(): Int {
        return R.id.container
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            navigateToWelcome()
        }
    }

    private fun navigateToWelcome() {
        val welcomeFragment = WelcomeFragment()
        TransitionHelper.setDefaultTransition(welcomeFragment)
        navigateTo(welcomeFragment, false)
    }

    override fun onBackPressed() {
        // Avoid cancelling loading fragment.
        if (lastNavigatedFragment is LoadingFragment) {
            return
        }
        super.onBackPressed()
    }
}
