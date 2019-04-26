package com.ccanahuire.retooh.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.ccanahuire.retooh.R
import com.ccanahuire.retooh.contract.UserDataFirebaseContract
import com.ccanahuire.retooh.model.UserData
import com.ccanahuire.retooh.ui.HomeActivity
import com.ccanahuire.retooh.ui.MainActivity
import com.ccanahuire.retooh.ui.RegistrationActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        Handler(Looper.getMainLooper()).postDelayed({
            checkSession()
        }, 1000)
    }

    private fun checkSession() {
        if (auth.currentUser != null) {
            val reference = database.reference

            val userDataListener = object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    auth.signOut()
                    navigateToInitialScreen()
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userData = dataSnapshot.getValue(UserData::class.java)
                    if (userData == null) {
                        navigateToRegistrationScreen()
                    } else {
                        navigateToHomeScreen(userData)
                    }
                }
            }

            reference.child(UserDataFirebaseContract.CHILD_USER).child(auth.currentUser!!.uid)
                .addListenerForSingleValueEvent(userDataListener)
        } else {
            navigateToInitialScreen()
        }
    }

    private fun navigateToInitialScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToRegistrationScreen() {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToHomeScreen(userData: UserData) {
        HomeActivity.start(this, userData)
        finish()
    }
}
