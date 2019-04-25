package com.ccanahuire.retooh.ui.register

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.ccanahuire.retooh.R
import com.ccanahuire.retooh.ui.HomeActivity
import com.ccanahuire.retooh.ui.NavigationHost

class RegisterBirthdateFragment: Fragment() {

    private var navigationHost: NavigationHost? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register_birthdate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnFinish: Button = view.findViewById(R.id.btn_finish)

        btnFinish.setOnClickListener { navigateToHome() }
    }

    override fun onAttach(context: Context?) {
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

    private fun navigateToHome() {
        if (context != null) {
            HomeActivity.start(context!!)
        }
    }

}