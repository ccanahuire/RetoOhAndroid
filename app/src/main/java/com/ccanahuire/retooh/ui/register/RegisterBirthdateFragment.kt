package com.ccanahuire.retooh.ui.register

import android.app.AlertDialog
import android.app.DatePickerDialog
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
import com.ccanahuire.retooh.ui.MainActivity
import com.ccanahuire.retooh.ui.NavigationHost
import com.ccanahuire.retooh.ui.components.watcher.OnChangeTextWatcher
import com.ccanahuire.retooh.ui.loading.LoadingFragment
import com.ccanahuire.retooh.utils.TransitionHelper
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

private const val USER_DATA = "userData"

class RegisterBirthdateFragment : Fragment() {

    companion object {
        fun newInstance(userData: UserData): RegisterBirthdateFragment {
            val fragment = RegisterBirthdateFragment()
            val args = Bundle()
            args.putSerializable(USER_DATA, userData)

            fragment.arguments = args

            return fragment
        }
    }

    private var navigationHost: NavigationHost? = null
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    private lateinit var birthDateEditText: EditText
    private lateinit var birthDateTextInputLayout: TextInputLayout

    private lateinit var userDataArg: UserData
    private var selectedPicker: Triple<Int, Int, Int>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        userDataArg = arguments?.getSerializable(USER_DATA) as UserData
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register_birthdate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        birthDateEditText = view.findViewById(R.id.et_birthdate)
        birthDateTextInputLayout = view.findViewById(R.id.til_birthdate)
        val btnFinish: Button = view.findViewById(R.id.btn_finish)

        birthDateEditText.addTextChangedListener(OnChangeTextWatcher {
            birthDateTextInputLayout.error = null
        })

        btnFinish.setOnClickListener {
            if (validateForm()) {
                userDataArg.birthdate = birthDateEditText.text.toString()
                saveUserDataIntoRemoteDatabase(userDataArg)
            }
        }

        birthDateEditText.setOnClickListener {
            showDatePicker()
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

    private fun showDatePicker() {
        if (context == null) {
            return
        }

        val dateListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            selectedPicker = Triple(year, month, dayOfMonth)
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val selectedDate = calendar.time

            userDataArg.age = calculateAge(calendar)
            birthDateEditText.setText(format.format(selectedDate))
        }

        if (selectedPicker == null) {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(context!!, dateListener, year, month, dayOfMonth)
            datePickerDialog.show()
        } else {
            val datePickerDialog = DatePickerDialog(context!!, dateListener, selectedPicker!!.first, selectedPicker!!.second, selectedPicker!!.third)
            datePickerDialog.show()
        }

    }

    private fun calculateAge(birthdateCalendar: Calendar): Int {
        val birthdateYear = birthdateCalendar.get(Calendar.YEAR)
        val birthdateMonth = birthdateCalendar.get(Calendar.MONTH)
        val birthdateDayOfMonth = birthdateCalendar.get(Calendar.DAY_OF_MONTH)

        val currentCalendar = Calendar.getInstance()
        val year = currentCalendar.get(Calendar.YEAR)
        val month = currentCalendar.get(Calendar.MONTH)
        val dayOfMonth = currentCalendar.get(Calendar.DAY_OF_MONTH)

        var age = year - birthdateYear

        if (month < birthdateMonth ||
            (month == birthdateMonth && dayOfMonth < birthdateDayOfMonth)) {
            age--
        }

        return age
    }

    private fun validateForm(): Boolean {
        if (birthDateEditText.text.isEmpty()) {
            birthDateTextInputLayout.error = getString(R.string.lang_register_birthdate_validation_required)
            return false
        }

        return true
    }

    private fun navigateToHome(userData: UserData) {
        if (context != null) {
            HomeActivity.start(context!!, userData)
            activity?.finish()
        }
    }

    private fun navigateToInit() {
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun saveUserDataIntoRemoteDatabase(userData: UserData) {
        if (auth.currentUser != null) {
            val reference = database.reference
            val loadingFragment = LoadingFragment()
            TransitionHelper.setDefaultTransition(loadingFragment)
            navigationHost?.navigateTo(loadingFragment, true)
            reference.child(UserDataFirebaseContract.CHILD_USER).child(auth.currentUser!!.uid).setValue(userData).addOnCompleteListener {
                navigateToHome(userData)
            }.addOnFailureListener {
                AlertDialog.Builder(context)
                    .setMessage(R.string.lang_register_birthdate_service_unavailable)
                    .setPositiveButton(getString(R.string.lang_common_action_ok)) { _, _ ->
                        navigationHost?.navigateBack()
                    }
                    .setCancelable(false)
                    .show()
            }

        } else {
            Toast.makeText(context, getString(R.string.lang_common_action_logged_out), Toast.LENGTH_SHORT).show()
            navigateToInit()
        }
    }

}