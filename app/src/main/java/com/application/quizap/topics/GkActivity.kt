package com.application.quizap.topics

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.text.HtmlCompat
import androidx.core.view.children
import com.application.quizap.R
import com.application.quizap.retro.ApiService
import com.application.quizap.retro.QuestionResponse
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_gk.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import kotlin.math.round

class GkActivity : AppCompatActivity() {
    lateinit var correctAns: String
    lateinit var correctChip: Chip
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gk)

        supportActionBar?.hide()
        var scoreQuestions = findViewById<TextView>(R.id.scoregkQuestions)
        var scoreAnswers = findViewById<TextView>(R.id.scoregkAnswers)
        var scoreTotal = findViewById<TextView>(R.id.scoregkTotal)
        var scoreQuestion = 1
        var scoreAnswer = 0.0F
        loadQuestion()
        btngkSubmit.setOnClickListener {
            try {
                var chip: Chip = cgOptiongk.findViewById(cgOptiongk.checkedChipId)
                if (chip.text == correctAns) {
                    Toast.makeText(
                        this,
                        chip.text.toString() + " is correct answer. WELL DONE!!",
                        Toast.LENGTH_LONG
                    ).show()
                    chip.chipBackgroundColor =
                        AppCompatResources.getColorStateList(this, R.color.correct)
                    scoreAnswer += 1
                } else {
                    Toast.makeText(
                        this,
                        chip.text.toString() + " is incorrect. SORRY!!",
                        Toast.LENGTH_LONG
                    ).show()
                    chip.chipBackgroundColor =
                        AppCompatResources.getColorStateList(this, R.color.incorrect)
                    for (a in cgOptiongk.children) {
                        var chips = a as Chip
                        if (chips.text.toString() == correctAns) {
                            correctChip = chips
                        }
                        chips.isClickable = false
                    }
                    correctChip.chipBackgroundColor =
                        AppCompatResources.getColorStateList(this, R.color.correct)
                }
                btngkSubmit.isClickable = false
                scoreAnswers.text = scoreAnswer.toString()
                scoreTotal.text = round(scoreAnswer / scoreQuestion * 100).toString()
            } catch (e: Exception) {
                Toast.makeText(this@GkActivity, "No Option Selected", Toast.LENGTH_LONG)
                    .show()
            }
        }
        btngkNext.setOnClickListener {
            cgOptiongk.removeAllViewsInLayout()
            loadQuestion()
            btngkSubmit.isClickable = true
            scoreQuestion += 1
            scoreQuestions.text = scoreQuestion.toString()
        }
        btngkQuit.setOnClickListener {
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
        api.fetchGkQuestion().enqueue(object : Callback<QuestionResponse> {
            override fun onResponse(
                call: Call<QuestionResponse>,
                response: Response<QuestionResponse>
            ) {
                //Log.e("msg","Success")
                //var result = QuestionResponse(response.body().results)->
                var result = response.body()?.results?.let { QuestionResponse(it) }
                if (result != null) {
                    if (Build.VERSION.SDK_INT>=24) {
                        gkQuestion.text = HtmlCompat.fromHtml(result.results[0].question,HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                    }
                    correctAns = result.results[0].correct_answer
                    var options = result.results[0].incorrect_answers
                    options.add(correctAns)
                    options.shuffled()
                    for (a in options) {
                        var chip = Chip(this@GkActivity)
                        chip.text = HtmlCompat.fromHtml(a, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                        chip.setTextColor(resources.getColor(R.color.colorText))
                        chip.isCheckable = true
                        chip.chipBackgroundColor = AppCompatResources.getColorStateList(
                            this@GkActivity,
                            R.color.chip_theme
                        )
                        chip.isClickable = true
                        chip.isCheckedIconVisible = false
                        chip.textStartPadding=70.0f
                        chip.textEndPadding=70.0f
                        cgOptiongk.addView(chip)
                    }
                }
            }

            override fun onFailure(call: Call<QuestionResponse>, t: Throwable) {
                Log.e("msg", "Failure")
            }
        })
    }
}