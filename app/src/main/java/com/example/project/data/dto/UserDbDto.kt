package com.example.project.data.network.dto

import com.google.gson.annotations.SerializedName

data class UserDbDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password") // Как в твоем db.json
    val password: String
)
