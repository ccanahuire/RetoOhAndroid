package com.ccanahuire.retooh.ui.welcome

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.ccanahuire.retooh.R
import com.ccanahuire.retooh.ui.NavigationHost
import com.ccanahuire.retooh.ui.login.LoginFragment
import com.ccanahuire.retooh.utils.TransitionHelper

class WelcomeFragment : Fragment() {

    private var navigationHost: NavigationHost? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val nextButton: Button = view.findViewById(R.id.btn_next)

        nextButton.setOnClickListener {
            navigateToLogin()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NavigationHost) {
            navigationHost = context
        } else {
            throw RuntimeException("$context must implement NavigationHost")
        }
    }

    override fun onDetach() {
        super.onDetach()
        navigationHost = null
    }

    private fun navigateToLogin() {
        val loginFragment = LoginFragment()
        TransitionHelper.setDefaultTransition(loginFragment)
        navigationHost?.navigateTo(loginFragment, true)
    }
}