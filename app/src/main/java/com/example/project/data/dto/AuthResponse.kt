package com.example.project.data.network.dto

/**
 * Data class для ответа сервера после успешной аутентификации.
 * Содержит информацию о пользователе и токен доступа.
 */
data class AuthResponse(
    val userId: String,         // Уникальный идентификатор пользователя от Mock API
    val token: String,          // Токен доступа для последующих запросов
    val email: String? = null,  // Email пользователя (опционально, если сервер его возвращает)
    // Ты можешь добавить сюда другие поля, которые может возвращать твой Mock API
    // например, displayName, registrationDate и т.д.
    val message: String? = null // Опционально, для сообщений об успехе от сервера
)
