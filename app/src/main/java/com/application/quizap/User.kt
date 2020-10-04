package com.application.quizap

import android.net.Uri

class User {
    lateinit var name: String
    lateinit var email: String
    lateinit var photoUrl: String

    constructor() {}

    constructor(name: String, email: String, url: String) {
        this.name = name
        this.email = email
        this.photoUrl = url
    }
}