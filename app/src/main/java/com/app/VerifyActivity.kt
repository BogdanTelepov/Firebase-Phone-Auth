package com.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.app.databinding.ActivityMainBinding
import com.app.databinding.ActivityVerifyBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class VerifyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        init()

    }


    private fun init() {
        auth = FirebaseAuth.getInstance()
        val storedVerificationId = intent.getStringExtra("storedVerificationId")

        val btnVerify = binding.btnVerify
        val otpNumber = binding.otpPhoneNumber
        btnVerify.setOnClickListener {
            val otp = otpNumber.text.toString().trim()
            if (otp.isNotEmpty()) {
                val credential: PhoneAuthCredential =
                    PhoneAuthProvider.getCredential(storedVerificationId.toString(), otp)
                signInWithPhone(credential)
            } else {
                Toast.makeText(this, "Enter Otp", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun signInWithPhone(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(applicationContext, HomeActivity::class.java))
                    finish()
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, "Invalid Otp", Toast.LENGTH_SHORT).show()
                    }
                }

            }
    }
}