package com.example.uas_ali

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uas_ali.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.buttonLogin.setOnClickListener {
            val email: String = binding.editTextTextEmailAddress.text.toString().trim()
            val password: String = binding.editTextTextPassword.text.toString().trim()

            if (email.isEmpty()) {
                binding.editTextTextEmailAddress.error = "Input Email"
                binding.editTextTextEmailAddress.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.editTextTextEmailAddress.error = "Invalid Email"
                binding.editTextTextEmailAddress.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.editTextTextPassword.error = "Input Password"
                binding.editTextTextPassword.requestFocus()
                return@setOnClickListener
            }

            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Login successful, navigate to ContentActivity
                        val intent = Intent(this, ContentActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Login failed, show error message
                        Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.textRegister.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
