package com.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.app.databinding.ActivityMainBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        init()
    }




    private fun init() {
        auth = FirebaseAuth.getInstance()
        val btnLogin = binding.btnLogin
        var currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(applicationContext, HomeActivity::class.java))
            finish()
        }

        btnLogin.setOnClickListener {
            login()
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                startActivity(Intent(applicationContext, HomeActivity::class.java))
                finish()
            }

            override fun onVerificationFailed(firebaseException: FirebaseException) {
                Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d("TAG", "onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token
                val intent = Intent(applicationContext, VerifyActivity::class.java)
                intent.putExtra("storedVerificationId", verificationId)
                startActivity(intent)
            }

        }

    }

    private fun login() {
        val mobileNumber = binding.editPhoneNumber
        var number = mobileNumber.text.toString().trim()

        if (number.isNotEmpty()) {
            number = "+996$number"
            sendCode(number)
        } else {
            Toast.makeText(this, "Enter mobile number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendCode(number: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

}