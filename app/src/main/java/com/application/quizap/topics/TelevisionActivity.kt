package com.application.quizap.topics

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.children
import com.application.quizap.R
import com.application.quizap.retro.ApiService
import com.application.quizap.retro.QuestionResponse
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_television.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.round

class TelevisionActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var correctAns: String
    var correctChip: Button? = null
    lateinit var btnOptions: LinearLayout
    var scoreQuestion: Int = 1
    var scoreAnswer: Int = 0
    private var highScore: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_television)

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            highScoreTv.visibility = View.GONE
            scoreT4.visibility = View.GONE
        }
        topBarTv.setNavigationOnClickListener { finish() }
        val prefs = getSharedPreferences("tvPref", Context.MODE_PRIVATE)
        val edit = prefs.edit()
        highScore = prefs.getInt("highScore", 0)
        highScoreTv.text = highScore.toString()
        btnOptions = findViewById(R.id.cgOptionTv)
        btnOptions.gravity = Gravity.CENTER_HORIZONTAL
        loadQuestion()
        btnTvNext.setOnClickListener {
            btnOptions.removeAllViewsInLayout()
            loadQuestion()
            scoreQuestion += 1
            scoreTvQuestions.text = scoreQuestion.toString()
            scoreTvTotal.text = ((scoreAnswer * 100) / scoreQuestion).toString()
            if (scoreTvQuestions.text.toString().toInt() >= 10) {
                if (scoreTvTotal.text.toString().toInt() > highScore) {
                    highScore = scoreTvTotal.text.toString().toInt()
                    highScoreTv.text = highScore.toString()
                    edit.putInt("highScore", highScore).apply()
                }
            }
        }
        btnTvQuit.setOnClickListener {
            MaterialAlertDialogBuilder(this).setTitle("Are you sure you want to quit this quiz?")
                .setPositiveButton("Yes") { _, _ ->
                    finish()
                }.setNegativeButton("No") { _, _ -> }
                .show()
        }
    }

    private fun loadQuestion() {
        val retro = Retrofit.Builder()
            .baseUrl("https://opentdb.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retro.create(ApiService::class.java)
        api.fetchTelevisionQuestion().enqueue(object : Callback<QuestionResponse> {
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onResponse(
                call: Call<QuestionResponse>,
                response: Response<QuestionResponse>
            ) {
                //var result = QuestionResponse(response.body().results)->
                var result = response.body()?.results?.let { QuestionResponse(it) }
                if (result != null) {
                    if (Build.VERSION.SDK_INT >= 24) {
                        tvQuestion.text = HtmlCompat.fromHtml(
                            result.results[0].question,
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        ).toString()
                    }
                    correctAns = result.results[0].correct_answer
                    var options = result.results[0].incorrect_answers
                    options.add(correctAns)
                    options.shuffle()
                    for (a in options) {
                        var chip = Button(this@TelevisionActivity)
                        chip.text =
                            HtmlCompat.fromHtml(a, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                        chip.setTextColor(resources.getColor(R.color.colorText))
                        chip.textAlignment = View.TEXT_ALIGNMENT_CENTER
                        chip.isClickable = false
                        chip.gravity = Gravity.CENTER_HORIZONTAL
                        chip.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        chip.layoutParams.resolveLayoutDirection(Gravity.CENTER_HORIZONTAL)
                        chip.setOnClickListener { checkAnswer(correctAns, chip) }
                        btnOptions.addView(chip)
                    }
                }
            }

            override fun onFailure(call: Call<QuestionResponse>, t: Throwable) {
                Log.e("msg", "Failure")
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun checkAnswer(correct: String, chip: Button) {
        for (a in btnOptions.children) {
            var child = a as Button
            if (child.text.toString() == correct)
                correctChip = child
            child.isClickable = false
        }
        if (chip.text == correct) {
            Toast.makeText(
                this,
                chip.text.toString() + " is correct answer. WELL DONE!!",
                Toast.LENGTH_LONG
            ).show()
            chip.setBackgroundResource(R.color.correct)
            scoreAnswer += 1
        } else {
            Toast.makeText(
                this,
                chip.text.toString() + " is incorrect. SORRY!!",
                Toast.LENGTH_LONG
            ).show()
            chip.setBackgroundResource(R.color.incorrect)
            correctChip?.setBackgroundResource(R.color.correct)
        }
        scoreTvAnswers.text = scoreAnswer.toString()
        scoreTvTotal.text = ((scoreAnswer * 100) / scoreQuestion).toString()
    }
}