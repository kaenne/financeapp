package com.example.project.ui.auth // Убедись, что пакет правильный

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project.data.AuthRepository
import com.example.project.data.ApiResult
import com.example.project.data.network.dto.AuthResponse
import kotlinx.coroutines.launch


class AuthViewModel : ViewModel() {

    private val repository = AuthRepository() // Используем наш обновленный AuthRepository

    // LiveData для результата регистрации
    private val _registrationResult = MutableLiveData<ApiResult<AuthResponse>>()
    val registrationResult: LiveData<ApiResult<AuthResponse>> = _registrationResult

    // LiveData для результата входа
    private val _loginResult = MutableLiveData<ApiResult<AuthResponse>>()
    val loginResult: LiveData<ApiResult<AuthResponse>> = _loginResult

    // LiveData для отслеживания состояния загрузки (выполнения запроса)
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun register(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            // Явно указываем тип
            val result: ApiResult<AuthResponse> = repository.registerUser(email, password)
            _registrationResult.postValue(result)
            _isLoading.postValue(false)
        }
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            // Явно указываем тип
            val result: ApiResult<AuthResponse> = repository.loginUser(email, password)
            _loginResult.postValue(result)
            _isLoading.postValue(false)
        }
    }
}
