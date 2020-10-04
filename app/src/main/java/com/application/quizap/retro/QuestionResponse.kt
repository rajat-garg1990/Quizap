package com.application.quizap.retro

data class QuestionResponse(val results: List<Question>)
/*
data class QuestionResult(
    val responseCode: Int,
    var result: Question
)*/

data class Question(
    var question: String,
    var correct_answer: String,
    var incorrect_answers: ArrayList<String>
)