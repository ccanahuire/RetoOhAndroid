package com.ccanahuire.retooh.ui.fingerprint

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.os.CancellationSignal
import com.ccanahuire.retooh.R
import com.ccanahuire.retooh.model.UserData
import com.ccanahuire.retooh.ui.HomeActivity
import com.ccanahuire.retooh.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth
import java.io.IOException
import java.security.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.security.cert.CertificateException




private const val EXTRA_USER_DATA = "userData"

private const val KEYSTORE_KEY_NAME = "biometric_key_name"

class FingerprintValidationActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context, userData: UserData) {
            val intent = Intent(context, FingerprintValidationActivity::class.java)
            intent.putExtra(EXTRA_USER_DATA, userData)
            context.startActivity(intent)
        }
    }

    private lateinit var userDataExtra: UserData

    private lateinit var errorTextView: TextView
    private var keyGenerator: KeyGenerator? = null
    private var keyStore: KeyStore? = null
    private var cipher: Cipher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fingerprint_validation)
        userDataExtra = intent.getSerializableExtra(EXTRA_USER_DATA) as UserData

        errorTextView = findViewById(R.id.tv_error)
        val signOutButton: Button = findViewById(R.id.btn_sign_out)

        val auth = FirebaseAuth.getInstance()

        signOutButton.setOnClickListener {
            auth.signOut()
            navigateToInitialScreen()
        }

        val fingerprintManagerCompat = FingerprintManagerCompat.from(this)
        generateKey()
        if (initCipher()) {
            val cryptoObject = FingerprintManagerCompat.CryptoObject(cipher!!)

            fingerprintManagerCompat.authenticate(
                cryptoObject, 0, CancellationSignal(),
                object : FingerprintManagerCompat.AuthenticationCallback() {
                    override fun onAuthenticationError(errMsgId: Int, errString: CharSequence?) {
                        super.onAuthenticationError(errMsgId, errString)
                        errorTextView.text = errString
                    }

                    override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence?) {
                        super.onAuthenticationHelp(helpMsgId, helpString)
                        errorTextView.text = helpString.toString()
                    }

                    override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult?) {
                        super.onAuthenticationSucceeded(result)
                        navigateToHomeScreen(userDataExtra)
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        errorTextView.setText(R.string.lang_fingerprint_validation_fingerprint_mismatch)
                    }
                }, null
            )
        }
    }

    private fun navigateToHomeScreen(userData: UserData) {
        HomeActivity.start(this, userData)
        finish()
    }

    private fun navigateToInitialScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore?.load(null)

            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            keyGenerator?.init(
                KeyGenParameterSpec.Builder(KEYSTORE_KEY_NAME, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build()
            )

            keyGenerator?.generateKey()

        } catch (exc: KeyStoreException) {
            exc.printStackTrace()
        } catch (exc: NoSuchAlgorithmException) {
            exc.printStackTrace()
        } catch (exc: NoSuchProviderException) {
            exc.printStackTrace()
        } catch (exc: InvalidAlgorithmParameterException) {
            exc.printStackTrace()
        } catch (exc: CertificateException) {
            exc.printStackTrace()
        } catch (exc: IOException) {
            exc.printStackTrace()
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun initCipher(): Boolean {
        try {
            cipher = Cipher.getInstance(
                KeyProperties.KEY_ALGORITHM_AES + "/"
                        + KeyProperties.BLOCK_MODE_CBC + "/"
                        + KeyProperties.ENCRYPTION_PADDING_PKCS7
            )

        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to get Cipher", e)
        } catch (e: NoSuchPaddingException) {
            throw RuntimeException("Failed to get Cipher", e)
        }

        try {
            keyStore?.load(
                null
            )
            val key = keyStore?.getKey(KEYSTORE_KEY_NAME, null) as SecretKey
            cipher?.init(Cipher.ENCRYPT_MODE, key)
            return true


        } catch (e: KeyPermanentlyInvalidatedException) {
            return false

        } catch (e: KeyStoreException) {

            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: CertificateException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: UnrecoverableKeyException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: IOException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: InvalidKeyException) {
            throw RuntimeException("Failed to init Cipher", e)
        }
    }
}