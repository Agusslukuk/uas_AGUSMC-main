package com.example.uas_ali

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uas_ali.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private fun registerUser(email: String, password: String) {

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                Intent(this, MainActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                }
            } else {
                Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.buttonRegister.setOnClickListener{

            val email : String = binding.editTextTextEmailAddress.text.toString().trim()
            val password: String = binding.editTextTextPassword.text.toString().trim()
            val repassword: String = binding.editTextTextPassword2.text.toString().trim()

            if (email.isEmpty()){
                binding.editTextTextEmailAddress.error = "Input Email"
                binding.editTextTextEmailAddress.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.editTextTextEmailAddress.error = "Invalid Email"
                binding.editTextTextEmailAddress.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty() || password.length < 6){
                binding.editTextTextPassword.error = "password must be more than 6 characters"
                binding.editTextTextPassword.requestFocus()
                return@setOnClickListener
            }

            if (password != repassword){
                binding.editTextTextPassword2.error = "password must be match"
                binding.editTextTextPassword2.requestFocus()
                return@setOnClickListener
            }
            registerUser(email, password)
        }

        binding.textLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}





