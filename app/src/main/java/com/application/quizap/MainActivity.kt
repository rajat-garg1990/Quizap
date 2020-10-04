package com.application.quizap

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.application.quizap.topics.GeographyActivity
import com.application.quizap.topics.GkActivity
import com.application.quizap.topics.SportsActivity
import com.application.quizap.topics.VgameActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var login = findViewById<Button>(R.id.login)
        var register = findViewById<Button>(R.id.register)
        auth= FirebaseAuth.getInstance()

        cardSports.setOnClickListener {
            startActivity(Intent(this, SportsActivity::class.java))
        }
        cardGk.setOnClickListener {
            startActivity(Intent(this, GkActivity::class.java))
        }
        cardVideoGames.setOnClickListener {
            startActivity(Intent(this, VgameActivity::class.java))
        }
        cardGeography.setOnClickListener {
            startActivity(Intent(this, GeographyActivity::class.java))
        }
        login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser!=null){
            startActivity(Intent(this,DashboardActivity::class.java))
            finish()
        }
    }
}