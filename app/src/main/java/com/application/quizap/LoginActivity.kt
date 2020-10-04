package com.application.quizap

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()
        topBar.setNavigationOnClickListener { finish() }
        var textUsername = findViewById<TextView>(R.id.textUsername)
        var textPassword = findViewById<TextView>(R.id.textPassword)
        var forgotButton = findViewById<Button>(R.id.forgotButton)
        var login = findViewById<Button>(R.id.loginButton)
        auth = FirebaseAuth.getInstance()
        login.setOnClickListener {
            if (textUsername.text.isEmpty() || textPassword.text.isEmpty()) {
                MaterialAlertDialogBuilder(this).setTitle("Fields cannot be blank !")
                    .setPositiveButton("OK") { _, _ -> }.show()
            } else {
                auth.signInWithEmailAndPassword(
                    textUsername.text.toString(),
                    textPassword.text.toString()
                )
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            updateUI(user)
                        } else {
                            Toast.makeText(
                                baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                            updateUI(null)
                        }
                    }
            }
        }
        forgotButton.setOnClickListener {
            val view = layoutInflater.inflate(R.layout.change_password, null)
            MaterialAlertDialogBuilder(this).setTitle("Enter Email ID :")
                .setView(view)
                .setPositiveButton("Reset") { _, _ ->
                    var mail = view.findViewById<EditText>(R.id.change_password)
                    if (mail != null) {
                        if (!TextUtils.isEmpty(mail.text.toString()) && android.util.Patterns.EMAIL_ADDRESS.matcher(
                                mail.text.toString()
                            ).matches()
                        ) {
                            auth.sendPasswordResetEmail(mail.text.toString())
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(this, "Email sent", Toast.LENGTH_LONG).show()
                                    }
                                }
                        }
                    }
                }
                .setNegativeButton("Cancel") { _, _ -> }
                .show()
        }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            if (currentUser.isEmailVerified) {
                finish()
                startActivity(Intent(this, DashboardActivity::class.java))
            } else {
                Toast.makeText(
                    this,
                    "User Email is not verified. Please verify your email !",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            MaterialAlertDialogBuilder(this).setTitle("User credentials are incorrect")
                .setPositiveButton("OK") { _, _ -> }
                .show()
        }
    }
}