package com.example.project.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        // Используем новое имя для SharedPreferences, чтобы избежать конфликтов со старой сессией Firebase
        private const val PREFS_NAME = "AppMockApiSession" // Проверь это имя
        private const val KEY_USER_ID = "mock_api_user_id"
        private const val KEY_AUTH_TOKEN = "mock_api_auth_token"
        private const val KEY_USER_EMAIL = "mock_api_user_email"
    }

    fun createLoginSession(userId: String, token: String, email: String?) {
        val editor = prefs.edit()
        editor.putString(KEY_USER_ID, userId)
        editor.putString(KEY_AUTH_TOKEN, token)
        if (email != null) {
            editor.putString(KEY_USER_EMAIL, email)
        } else {
            editor.remove(KEY_USER_EMAIL)
        }
        editor.apply()
    }

    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    fun getToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null && getUserId() != null
    }

    fun clearSession() {
        val editor = prefs.edit()
        editor.remove(KEY_USER_ID)
        editor.remove(KEY_AUTH_TOKEN)
        editor.remove(KEY_USER_EMAIL)
        editor.apply()
    }
}
