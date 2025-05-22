package com.example.project.data.network.dto

/**
 * Data class для представления стандартного ответа об ошибке от API.
 */
data class ErrorResponse(
    val error: String?,         // Основное сообщение об ошибке
    val message: String?,       // Более детальное сообщение или массив сообщений
    val code: Int? = null,      // Внутренний код ошибки API (не HTTP статус)
    val details: Map<String, String>? = null // Дополнительные детали, например, по полям
)
