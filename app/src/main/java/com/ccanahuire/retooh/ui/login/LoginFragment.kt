package com.ccanahuire.retooh.ui.login


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.ccanahuire.retooh.R
import com.ccanahuire.retooh.ui.NavigationHost
import com.ccanahuire.retooh.ui.components.watcher.OnChangeTextWatcher
import com.ccanahuire.retooh.ui.components.watcher.OnReachMaxCharacterTextWatcher
import com.ccanahuire.retooh.ui.components.watcher.PhoneFormatterTextWatcher
import com.ccanahuire.retooh.ui.register.RegisterNameFragment
import com.ccanahuire.retooh.utils.InputUtils
import com.ccanahuire.retooh.utils.TransitionHelper
import com.google.android.material.textfield.TextInputLayout

class LoginFragment : Fragment() {

    private var navigationHost: NavigationHost? = null

    private lateinit var phoneNumberEditText: EditText
    private lateinit var phoneNumberTextInputLayout: TextInputLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val signInButton: Button = view.findViewById(R.id.btn_sign_in)
        val signInFacebookButton: Button = view.findViewById(R.id.btn_sign_in_facebook)
        phoneNumberEditText = view.findViewById(R.id.et_phone_number)
        phoneNumberTextInputLayout = view.findViewById(R.id.til_phone_number)

        phoneNumberEditText.addTextChangedListener(PhoneFormatterTextWatcher())
        phoneNumberEditText.addTextChangedListener(OnChangeTextWatcher {
            phoneNumberTextInputLayout.error = null
        })
        phoneNumberEditText.addTextChangedListener(OnReachMaxCharacterTextWatcher(11) {
            InputUtils.hideSoftKeyboard(context, phoneNumberEditText)
        })

        signInButton.setOnClickListener {
            if (validateSignIn()) {
                navigateToRegisterName()
            }
        }
        signInFacebookButton.setOnClickListener { navigateToRegisterName() }
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

    private fun validateSignIn(): Boolean {
        if (phoneNumberEditText.text.length < 11) {
            phoneNumberTextInputLayout.error = getString(R.string.lang_login_phone_number_validation_length)
            return false
        }

        return true
    }

    private fun navigateToRegisterName() {
        val registerNameFragment = RegisterNameFragment()
        TransitionHelper.setDefaultTransition(registerNameFragment)
        navigationHost?.navigateTo(registerNameFragment, true)
    }
}
