package com.example.project.data.network

import android.content.Context
//import com.example.project.BuildConfig
import com.example.project.utils.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // !!! ЗАМЕНИ НА URL ТВОЕГО MOCK API СЕРВЕРА !!!
    // Для эмулятора Android, если сервер запущен на твоем компьютере (localhost):
    private const val MOCK_API_BASE_URL = "http://10.0.2.2:3000/"

    private var sessionManagerInstance: SessionManager? = null

    /**
     * Инициализирует RetrofitClient, предоставляя контекст для создания SessionManager.
     * Этот метод должен быть вызван один раз при старте приложения, например, в Application.onCreate().
     */
    fun initialize(context: Context) {
        if (sessionManagerInstance == null) {
            // Создаем экземпляр SessionManager, который будет использоваться в AuthInterceptor
            sessionManagerInstance = SessionManager(context.applicationContext)
        }
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // ВРЕМЕННО ЗАКОММЕНТИРУЕМ ИЛИ ИЗМЕНИМ ДЛЯ ТЕСТА СБОРКИ
        // level = if (BuildConfig.DEBUG) {
        //     HttpLoggingInterceptor.Level.BODY
        // } else {
        //     HttpLoggingInterceptor.Level.NONE
        // }
        level = HttpLoggingInterceptor.Level.BODY // Временно ставим BODY для всех сборок
    }

    // Interceptor для автоматического добавления токена авторизации к запросам
    private val authInterceptor = okhttp3.Interceptor { chain ->
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        // Получаем токен из SessionManager
        // Убедись, что в SessionManager есть метод getToken(), возвращающий String?
        sessionManagerInstance?.getToken()?.let { token ->
            if (token.isNotBlank()) { // Добавляем токен, только если он есть
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
        }
        // Для эндпоинтов, которые не требуют токен (например, login/register),
        // этот interceptor все равно будет срабатывать, но токен не добавится, если его нет.
        // Если нужно более тонкое управление, можно проверять URL запроса.

        chain.proceed(requestBuilder.build())
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)    // Логирование запросов и ответов
        .addInterceptor(authInterceptor)       // Добавление токена авторизации
        .connectTimeout(30, TimeUnit.SECONDS)  // Таймаут на подключение
        .readTimeout(30, TimeUnit.SECONDS)     // Таймаут на чтение ответа
        .writeTimeout(30, TimeUnit.SECONDS)    // Таймаут на запись запроса
        .build()

    // Lazy-инициализация экземпляра Retrofit
    val instance: ApiService by lazy {
        if (sessionManagerInstance == null) {
            // Это исключение поможет выявить, если initialize() не был вызван.
            throw IllegalStateException("RetrofitClient must be initialized with Context before accessing instance.")
        }
        Retrofit.Builder()
            .baseUrl(MOCK_API_BASE_URL) // Используем наш базовый URL
            .client(okHttpClient)       // Используем настроенный OkHttpClient
            .addConverterFactory(GsonConverterFactory.create()) // Используем Gson для (де)сериализации
            .build()
            .create(ApiService::class.java) // Создаем реализацию нашего ApiService интерфейса
    }
}
