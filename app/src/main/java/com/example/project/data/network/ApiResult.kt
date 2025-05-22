package com.example.project.data // или com.example.project.data.network

/**
 * Обертка для результатов API-запросов.
 * Позволяет обрабатывать состояния успеха и ошибки.
 * @param T Тип данных в случае успеха.
 */
sealed class ApiResult<out T> {
    /**
     * Представляет успешный результат API-запроса.
     * @param data Данные, полученные от сервера.
     */
    data class Success<out T>(val data: T) : ApiResult<T>()

    /**
     * Представляет ошибку при выполнении API-запроса.
     * @param message Сообщение об ошибке.
     * @param code HTTP-статус код ошибки (опционально).
     * @param errorBody Сырое тело ошибки от сервера (опционально, для более детального анализа).
     */
    data class Error(
        val message: String,
        val code: Int? = null,
        val errorBody: String? = null // Можно добавить, если нужно передавать тело ошибки
    ) : ApiResult<Nothing>()

    /**
     * Представляет состояние загрузки (опционально, если хочешь явно его передавать).
     */
    // object Loading : ApiResult<Nothing>() // Если решишь использовать
}
