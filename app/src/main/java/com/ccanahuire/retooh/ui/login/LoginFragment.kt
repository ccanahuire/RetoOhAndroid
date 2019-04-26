package com.ccanahuire.retooh.ui.login


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ccanahuire.retooh.R
import com.ccanahuire.retooh.contract.UserDataFirebaseContract
import com.ccanahuire.retooh.model.UserData
import com.ccanahuire.retooh.ui.HomeActivity
import com.ccanahuire.retooh.ui.NavigationHost
import com.ccanahuire.retooh.ui.RegistrationActivity
import com.ccanahuire.retooh.ui.components.watcher.OnChangeTextWatcher
import com.ccanahuire.retooh.ui.components.watcher.OnReachMaxCharacterTextWatcher
import com.ccanahuire.retooh.ui.components.watcher.PhoneFormatterTextWatcher
import com.ccanahuire.retooh.ui.loading.LoadingFragment
import com.ccanahuire.retooh.ui.phone.PhoneValidationFragment
import com.ccanahuire.retooh.utils.InputUtils
import com.ccanahuire.retooh.utils.TransitionHelper
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class LoginFragment : Fragment() {
    private var navigationHost: NavigationHost? = null

    private lateinit var phoneNumberEditText: EditText
    private lateinit var phoneNumberTextInputLayout: TextInputLayout

    private lateinit var callbackManager: CallbackManager
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                signInWithFirebaseCredentials(credential)
            }

            override fun onCancel() {
            }

            override fun onError(error: FacebookException) {
                throw error
            }
        })
    }

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
            if (validateForm()) {
                navigateToPhoneValidation(phoneNumberEditText.text.toString())
            }
        }
        signInFacebookButton.setOnClickListener { signInWithFacebook() }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun validateForm(): Boolean {
        if (phoneNumberEditText.text.length < 11) {
            phoneNumberTextInputLayout.error = getString(R.string.lang_login_phone_number_validation_length)
            return false
        }

        return true
    }

    private fun navigateToRegisterName() {
        val intent = Intent(context, RegistrationActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun navigateToPhoneValidation(phone: String) {
        val fragment = PhoneValidationFragment.newInstance(phone)
        TransitionHelper.setDefaultTransition(fragment)
        navigationHost?.navigateTo(fragment, true)
    }

    private fun navigateToHomeScreen(userData: UserData) {
        if (context != null) {
            HomeActivity.start(context!!, userData)
            activity?.finish()
        }
    }

    private fun signInWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, arrayListOf("public_profile"))
    }

    private fun signInWithFirebaseCredentials(credential: AuthCredential) {
        val loadingFragment = LoadingFragment()
        TransitionHelper.setDefaultTransition(loadingFragment)
        navigationHost?.navigateTo(loadingFragment, true)

        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                retrieveUserData()
            } else {
                navigationHost?.navigateBack()
                Toast.makeText(
                    context, "Authentication failed.",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }

    private fun retrieveUserData() {
        val reference = database.reference

        val userDataListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                navigationHost?.navigateBack()
                Toast.makeText(
                    context, "Authentication failed.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userData = dataSnapshot.getValue(UserData::class.java)
                if (userData == null) {
                    navigateToRegisterName()
                } else {
                    navigateToHomeScreen(userData)
                }
            }
        }

        reference.child(UserDataFirebaseContract.CHILD_USER).child(auth.currentUser!!.uid)
            .addListenerForSingleValueEvent(userDataListener)
    }

}
