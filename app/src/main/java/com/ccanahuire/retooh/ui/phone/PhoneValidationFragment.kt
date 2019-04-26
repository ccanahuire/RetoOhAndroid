package com.ccanahuire.retooh.ui.phone


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ccanahuire.retooh.R
import com.ccanahuire.retooh.ui.NavigationHost
import com.ccanahuire.retooh.ui.RegistrationActivity
import com.ccanahuire.retooh.ui.components.watcher.OnChangeTextWatcher
import com.ccanahuire.retooh.ui.components.watcher.OnReachMaxCharacterTextWatcher
import com.ccanahuire.retooh.utils.FormatUtils
import com.ccanahuire.retooh.utils.InputUtils
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

private const val ARG_PHONE = "phone"

class PhoneValidationFragment : Fragment() {
    private var navigationHost: NavigationHost? = null

    private var storedVerificationId: String? = null
    private var storedResendToken: PhoneAuthProvider.ForceResendingToken? = null

    private lateinit var auth: FirebaseAuth

    private lateinit var codeEditText: EditText
    private lateinit var codeTextInputLayout: TextInputLayout
    private lateinit var verifyButton: Button
    private lateinit var resendButton: Button
    private lateinit var loadingProgressBar: ProgressBar

    companion object {
        fun newInstance(phone: String): PhoneValidationFragment {
            val fragment = PhoneValidationFragment()
            val args = Bundle()
            args.putString(ARG_PHONE, phone)
            fragment.arguments = args

            return fragment
        }
    }

    private lateinit var phoneArg: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        auth.useAppLanguage()
        phoneArg = arguments?.getString(ARG_PHONE)!!
        sendVerificationCode(null)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone_validation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        codeEditText = view.findViewById(R.id.et_code)
        codeTextInputLayout = view.findViewById(R.id.til_code)
        loadingProgressBar = view.findViewById(R.id.pb_loading)
        verifyButton = view.findViewById(R.id.btn_verify)
        resendButton = view.findViewById(R.id.btn_resend)
        val messageTextView: TextView = view.findViewById(R.id.tv_message)

        messageTextView.text =
            getString(R.string.lang_phone_validation_message, FormatUtils.formatPhoneNumber(phoneArg))

        codeEditText.addTextChangedListener(OnChangeTextWatcher {
            codeTextInputLayout.error = null
        })
        codeEditText.addTextChangedListener(OnReachMaxCharacterTextWatcher(6) {
            InputUtils.hideSoftKeyboard(context, codeEditText)
        })

        verifyButton.setOnClickListener {
            if (validateForm()) {
                verifyCode()
            }
        }

        resendButton.setOnClickListener {
            sendVerificationCode(storedResendToken)
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
        if (codeEditText.text.length < 6) {
            codeTextInputLayout.error = getString(R.string.lang_phone_validation_code_validation_length)
            return false
        }

        return true
    }

    private fun verifyCode() {
        if (storedVerificationId != null) {
            val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, codeEditText.text.toString())
            signInWithFirebaseCredentials(credential)
        }
    }

    private fun sendVerificationCode(resendToken: PhoneAuthProvider.ForceResendingToken?) {
        // Creating a phone number. In this version it's hardcoded with Peru country code +51.
        val phoneNumber = "+51${FormatUtils.digitsOnly(phoneArg)}"

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithFirebaseCredentials(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                AlertDialog.Builder(context)
                    .setMessage(R.string.lang_phone_validation_code_unavailable)
                    .setPositiveButton(getString(R.string.lang_common_action_ok)) { _, _ ->
                        navigationHost?.navigateBack()
                    }
                    .setCancelable(false)
                    .show()
            }

            override fun onCodeSent(
                verificationId: String?,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                storedVerificationId = verificationId
                storedResendToken = token
            }
        }

        if (activity != null) {
            val timeout = 60L
            if (resendToken != null) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,
                    timeout,
                    TimeUnit.SECONDS,
                    activity!!,
                    callbacks,
                    resendToken
                )

                AlertDialog.Builder(context)
                    .setMessage(R.string.lang_phone_validation_code_resend_message)
                    .setPositiveButton(getString(R.string.lang_common_action_ok)) { _, _ ->
                        codeEditText.setText("")
                    }
                    .setCancelable(false)
                    .show()
            } else {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,
                    timeout,
                    TimeUnit.SECONDS,
                    activity!!,
                    callbacks
                )
            }
        }
    }

    private fun signInWithFirebaseCredentials(credential: AuthCredential) {
        displayLoading()
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                navigateToRegisterName()
            } else {
                hideLoading()
                codeTextInputLayout.error = getString(R.string.lang_phone_validation_code_validation_valid)
            }
        }
    }

    private fun navigateToRegisterName() {
        val intent = Intent(context, RegistrationActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun displayLoading() {
        loadingProgressBar.visibility = View.VISIBLE
        verifyButton.isEnabled = false
        resendButton.isEnabled = false
    }

    private fun hideLoading() {
        loadingProgressBar.visibility = View.GONE
        verifyButton.isEnabled = true
        resendButton.isEnabled = true
    }
}
