package com.example.project.data.network.dto

data class GenericApiResponse<T>(
    val status: String?,    // Например, "success", "error"
    val message: String?,   // Сообщение от сервера
    val data: T?            // Сами данные
)
