package com.example.project.data

import com.example.project.data.network.ApiService
import com.example.project.data.network.RetrofitClient
import com.example.project.data.network.dto.AuthRequest
import com.example.project.data.network.dto.AuthResponse
import com.example.project.data.network.dto.ErrorResponse
import com.example.project.data.network.dto.UserDbDto
import com.google.gson.Gson
import java.util.UUID

class AuthRepository {

    private val apiService: ApiService = RetrofitClient.instance

    private suspend fun <T : Any> safeApiCall(
        apiCall: suspend () -> retrofit2.Response<T>
    ): ApiResult<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                response.body()?.let {
                    ApiResult.Success(it)
                } ?: ApiResult.Error("Тело ответа пустое (null)", response.code())
            } else {
                val errorBodyString = response.errorBody()?.string()
                val errorMessage = if (errorBodyString != null) {
                    try {
                        val errorResponse = Gson().fromJson(errorBodyString, ErrorResponse::class.java)
                        errorResponse.message ?: errorResponse.error ?: "Неизвестная ошибка API (код ${response.code()})"
                    } catch (e: Exception) {
                        "Ошибка сервера (код ${response.code()}): ${response.message().ifEmpty { "Нет сообщения" }}"
                    }
                } else {
                    "Неизвестная ошибка сервера (код ${response.code()})"
                }
                ApiResult.Error(errorMessage, response.code(), errorBodyString)
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Ошибка сети или неизвестное исключение")
        }
    }

    suspend fun registerUser(email: String, password: String): ApiResult<AuthResponse> {
        // Первый when: проверка существующего пользователя
        // Этот when не является выражением, возвращающим значение для всей функции,
        // он используется для раннего return.
        when (val existingUserResult = safeApiCall { apiService.loginUser(email) }) {
            is ApiResult.Success -> {
                if (existingUserResult.data.isNotEmpty()) {
                    return ApiResult.Error("Пользователь с таким email уже существует")
                }
                // Если data пустая, ничего не делаем, идем дальше к регистрации
            }
            is ApiResult.Error -> {
                // Если при проверке произошла ошибка (например, сетевая), возвращаем ее
                return ApiResult.Error(existingUserResult.message, existingUserResult.code, existingUserResult.errorBody)
            }
            // Для when, который не является выражением, else не нужен, если все пути
            // либо покрывают sealed class, либо делают return.
            // Если компилятор все еще жалуется здесь, это странно.
            else -> {}
        }

        // Если дошли сюда, значит, email свободен
        val registrationRequest = AuthRequest(email, password)
        // Второй when: результат регистрации. Этот when *является* выражением, возвращающим значение.
        return when (val result = safeApiCall { apiService.registerUser(registrationRequest) }) {
            is ApiResult.Success -> {
                val userFromDb: UserDbDto = result.data
                val fakeToken = UUID.randomUUID().toString()
                ApiResult.Success(AuthResponse(userId = userFromDb.id, token = fakeToken, email = userFromDb.email))
            }
            is ApiResult.Error -> {
                ApiResult.Error(result.message, result.code, result.errorBody)
            }
            // Эта ветка не должна достигаться, если ApiResult sealed и safeApiCall корректен.
            // Добавляем ее, чтобы удовлетворить компилятор, если он настаивает.
            else -> ApiResult.Error("Неожиданное состояние результата регистрации", -1)
        }
    }

    suspend fun loginUser(email: String, passwordToVerify: String): ApiResult<AuthResponse> {
        // Этот when *является* выражением, возвращающим значение.
        return when (val result = safeApiCall { apiService.loginUser(email) }) {
            is ApiResult.Success -> {
                val users: List<UserDbDto> = result.data
                if (users.isNotEmpty()) {
                    val userFromDb = users[0]
                    if (userFromDb.password == passwordToVerify) {
                        val fakeToken = UUID.randomUUID().toString()
                        ApiResult.Success(AuthResponse(userId = userFromDb.id, token = fakeToken, email = userFromDb.email))
                    } else {
                        ApiResult.Error("Неверный пароль")
                    }
                } else {
                    ApiResult.Error("Пользователь с таким email не найден")
                }
            }
            is ApiResult.Error -> {
                ApiResult.Error(result.message, result.code, result.errorBody)
            }
            // Эта ветка не должна достигаться.
            else -> ApiResult.Error("Неожиданное состояние результата входа", -1)
        }
    }
}
