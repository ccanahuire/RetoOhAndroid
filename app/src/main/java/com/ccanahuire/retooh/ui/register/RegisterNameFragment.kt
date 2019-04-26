package com.ccanahuire.retooh.ui.register

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.ccanahuire.retooh.R
import com.ccanahuire.retooh.model.UserData
import com.ccanahuire.retooh.ui.NavigationHost
import com.ccanahuire.retooh.utils.TransitionHelper
import com.google.android.material.textfield.TextInputLayout

class RegisterNameFragment : Fragment() {

    private var navigationHost: NavigationHost? = null

    private lateinit var nameEditText: EditText
    private lateinit var nameTextInputLayout: TextInputLayout
    private lateinit var lastNameEditText: EditText
    private lateinit var lastNameTextInputLayout: TextInputLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register_name, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameEditText = view.findViewById(R.id.et_name)
        nameTextInputLayout = view.findViewById(R.id.til_name)
        lastNameEditText = view.findViewById(R.id.et_last_name)
        lastNameTextInputLayout = view.findViewById(R.id.til_last_name)
        val nextButton: Button = view.findViewById(R.id.btn_next)

        nextButton.setOnClickListener {
            if (validateForm()) {
                val userData = UserData(
                    nameEditText.text.toString(),
                    lastNameEditText.text.toString(),
                    null, null
                )

                navigateToRegisterBirthdate(userData)
            }
        }
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

    private fun validateForm(): Boolean {
        var result = true
        if (nameEditText.text.isEmpty()) {
            nameTextInputLayout.error = getString(R.string.lang_register_name_validation_name_required)
            result = false
        }
        if (lastNameEditText.text.isEmpty()) {
            lastNameTextInputLayout.error = getString(R.string.lang_register_name_validation_last_name_required)
            result = false
        }
        return result
    }

    private fun navigateToRegisterBirthdate(userData: UserData) {
        val registerBirthdateFragment = RegisterBirthdateFragment.newInstance(userData)
        TransitionHelper.setDefaultTransition(registerBirthdateFragment)
        navigationHost?.navigateTo(registerBirthdateFragment, true)
    }
}