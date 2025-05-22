package com.example.project.utils

import java.security.MessageDigest
import java.util.regex.Pattern

object PasswordUtils {

    // Простая SHA-256 хэш-функция
    fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    fun verifyPassword(passwordAttempt: String, storedHash: String): Boolean {
        return hashPassword(passwordAttempt) == storedHash
    }
}

object ValidationUtils {
    private const val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
    private val EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX)

    fun isValidEmail(email: String?): Boolean {
        return !email.isNullOrBlank() && EMAIL_PATTERN.matcher(email).matches()
    }

    // Пароль должен быть не менее 6 символов
    fun isValidPassword(password: String?): Boolean {
        return !password.isNullOrBlank() && password.length >= 6
    }
}
