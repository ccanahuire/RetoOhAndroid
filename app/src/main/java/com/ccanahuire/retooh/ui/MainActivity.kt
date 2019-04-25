package com.ccanahuire.retooh.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ccanahuire.retooh.R
import com.ccanahuire.retooh.ui.welcome.WelcomeFragment
import com.ccanahuire.retooh.utils.TransitionHelper

class MainActivity : AppCompatActivity(), NavigationHost {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            navigateToWelcome()
        }
    }

    override fun navigateTo(fragment: Fragment, addToBackstack: Boolean) {
        val transaction = supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)

        if (addToBackstack) {
            transaction.addToBackStack(null)
        }

        transaction.commit()
    }

    private fun navigateToWelcome() {
        val welcomeFragment = WelcomeFragment()
        TransitionHelper.setDefaultTransition(welcomeFragment)
        navigateTo(welcomeFragment, false)
    }
}
