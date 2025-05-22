package com.example.project.data.network.dto

import com.google.gson.annotations.SerializedName

/**
 * Data class для представления транзакции при обмене данными с API.
 */
data class TransactionApiDto(
    // Если имя поля в JSON отличается, используй @SerializedName
    // @SerializedName("server_id")
    val id: String?,            // Уникальный ID транзакции от сервера (может быть null при создании новой)
    val amount: Double,
    val type: String,           // "INCOME" или "EXPENSE"
    val category: String,
    val timestamp: Long,        // Временная метка в миллисекундах (Unix timestamp)
    val description: String? = null, // Опциональное описание транзакции
    val userId: String? = null  // ID пользователя, к которому относится транзакция.
    // Может не требоваться в теле запроса, если userId передается в URL
    // или определяется по токену на сервере.
    // Но может быть полезен в ответе от сервера.
)
