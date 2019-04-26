package com.ccanahuire.retooh.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ccanahuire.retooh.R
import com.ccanahuire.retooh.model.UserData
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

private const val EXTRA_USER_DATA = "userData"

class HomeActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context, userData: UserData) {
            val intent = Intent(context, HomeActivity::class.java)
            intent.putExtra(EXTRA_USER_DATA, userData)
            context.startActivity(intent)
        }
    }

    private lateinit var extraUserData: UserData

    private lateinit var welcomeTitleTextView: TextView
    private lateinit var nameTextView: TextView
    private lateinit var lastNameTextView: TextView
    private lateinit var ageTextView: TextView
    private lateinit var birthdateTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)

        val auth = FirebaseAuth.getInstance()

        welcomeTitleTextView = findViewById(R.id.tv_welcome_title)
        nameTextView = findViewById(R.id.tv_name)
        lastNameTextView = findViewById(R.id.tv_last_name)
        ageTextView = findViewById(R.id.tv_age)
        birthdateTextView = findViewById(R.id.tv_birthdate)
        val signOutButton: Button = findViewById(R.id.btn_sign_out)

        extraUserData = intent.getSerializableExtra(EXTRA_USER_DATA) as UserData

        signOutButton.setOnClickListener {
            auth.signOut()
            navigateToInitialScreen()
        }

        populateData(extraUserData)
    }

    private fun populateData(userData: UserData) {
        welcomeTitleTextView.text = getString(R.string.lang_home_welcome, userData.name)
        nameTextView.text = getString(R.string.lang_home_name, userData.name)
        lastNameTextView.text = getString(R.string.lang_home_last_name, userData.lastName)
        ageTextView.text = getString(R.string.lang_home_age, userData.age)
        birthdateTextView.text = getString(R.string.lang_home_birthdate, userData.birthdate)
    }

    private fun navigateToInitialScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
