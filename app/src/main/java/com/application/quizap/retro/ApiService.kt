package com.application.quizap.retro

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("/api.php?amount=1&category=21&difficulty=medium&type=multiple")
    fun fetchSportQuestion(): Call<QuestionResponse>

    @GET("/api.php?amount=1&category=9&difficulty=medium&type=multiple")
    fun fetchGkQuestion(): Call<QuestionResponse>

    @GET("/api.php?amount=1&category=15&difficulty=medium&type=multiple")
    fun fetchVideoGameQuestion(): Call<QuestionResponse>

    @GET("/api.php?amount=1&category=22&difficulty=medium&type=multiple")
    fun fetchGeographyQuestion(): Call<QuestionResponse>

    @GET("/api.php?amount=1&category=16&difficulty=medium&type=multiple")
    fun fetchBoardGameQuestion(): Call<QuestionResponse>

    @GET("/api.php?amount=1&category=19&difficulty=medium&type=multiple")
    fun fetchMathematicsQuestion(): Call<QuestionResponse>

    @GET("/api.php?amount=1&category=23&difficulty=medium&type=multiple")
    fun fetchHistoryQuestion(): Call<QuestionResponse>

    @GET("/api.php?amount=1&category=32&difficulty=medium&type=multiple")
    fun fetchAnimeQuestion(): Call<QuestionResponse>

    @GET("/api.php?amount=1&category=31&difficulty=medium&type=multiple")
    fun fetchCartoonQuestion(): Call<QuestionResponse>

    @GET("/api.php?amount=1&category=28&difficulty=medium&type=multiple")
    fun fetchVehicleQuestion(): Call<QuestionResponse>

    @GET("/api.php?amount=1&category=30&difficulty=medium&type=multiple")
    fun fetchGadgetQuestion(): Call<QuestionResponse>

    @GET("/api.php?amount=1&category=17&difficulty=medium&type=multiple")
    fun fetchNatureQuestion(): Call<QuestionResponse>

    @GET("/api.php?amount=1&category=14&difficulty=medium&type=multiple")
    fun fetchTelevisionQuestion(): Call<QuestionResponse>

    @GET("/api.php?amount=1&category=11&difficulty=medium&type=multiple")
    fun fetchFilmQuestion(): Call<QuestionResponse>

    @GET("/api.php?amount=1&category=12&difficulty=medium&type=multiple")
    fun fetchMusicQuestion(): Call<QuestionResponse>

    @GET("/api.php?amount=1&category=29&difficulty=medium&type=multiple")
    fun fetchComicsQuestion(): Call<QuestionResponse>

    @GET("/api.php?amount=1&category=24&difficulty=medium&type=multiple")
    fun fetchPoliticsQuestion(): Call<QuestionResponse>
}