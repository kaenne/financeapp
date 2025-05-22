package com.example.project.data.network

import com.example.project.data.network.dto.AuthRequest
import com.example.project.data.network.dto.AuthResponse
import com.example.project.data.network.dto.TransactionApiDto
import com.example.project.data.network.dto.UserDbDto
import retrofit2.Response // Важно: используем retrofit2.Response для получения полного HTTP-ответа
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query // Если понадобятся query-параметры

/**
 * Интерфейс, описывающий эндпоинты API для взаимодействия с сервером.
 * Retrofit будет использовать этот интерфейс для генерации кода сетевых запросов.
 */
interface ApiService {
    @POST("users") // Для регистрации создаем нового пользователя
    suspend fun registerUser(@Body request: AuthRequest): Response<UserDbDto> // Ожидаем UserDbDto, а не AuthResponse

    @GET("users") // Для логина ищем пользователя по email
    suspend fun loginUser(@Query("email") email: String /* , @Query("password") password: String */ ): Response<List<UserDbDto>> // Пароль будем проверять на клиенте

    @GET("transactions") // Получаем транзакции, фильтруем по userId
    suspend fun getTransactions(
        @Header("Authorization") token: String,
        @Query("userId") userId: String // Используем Query параметр
    ): Response<List<TransactionApiDto>>

    @POST("transactions") // Добавляем транзакцию
    suspend fun addTransaction(
        @Header("Authorization") token: String,
        // userId должен быть в теле TransactionApiDto
        @Body transactionDto: TransactionApiDto
    ): Response<TransactionApiDto>
}
