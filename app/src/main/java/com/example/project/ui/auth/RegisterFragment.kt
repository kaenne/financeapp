package com.example.project.ui.auth // Убедись, что пакет правильный

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.project.R
import com.example.project.WalletApp
import com.example.project.data.ApiResult // Наш ApiResult
import com.example.project.databinding.FragmentRegisterBinding
import com.example.project.utils.SessionManager

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        // Получаем экземпляр SessionManager из Application класса
        sessionManager = (requireActivity().application as WalletApp).sessionManager
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Если пользователь уже вошел, перенаправляем на главный экран
        if (sessionManager.isLoggedIn()) {
            // Убедись, что в nav_graph.xml этот action настроен на очистку бэкстека
            // (popUpTo, popUpToInclusive), чтобы пользователь не мог вернуться назад на экран регистрации.
            findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
            return // Важно выйти из метода, чтобы не настраивать UI и слушатели заново
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmailRegister.text.toString().trim()
            val password = binding.etPasswordRegister.text.toString().trim()
            val confirmPassword = binding.etConfirmPasswordRegister.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) { // Пример простой валидации пароля
                Toast.makeText(requireContext(), "Пароль должен быть не менее 6 символов", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(requireContext(), "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authViewModel.register(email, password)
        }

        binding.tvGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        // Наблюдаем за результатом регистрации
        authViewModel.registrationResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ApiResult.Success -> {
                    // Успешная регистрация
                    val authData = result.data
                    sessionManager.createLoginSession(authData.userId, authData.token, authData.email)
                    Toast.makeText(requireContext(), "Регистрация успешна!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                }
                is ApiResult.Error -> {
                    // Ошибка регистрации
                    Toast.makeText(requireContext(), "Ошибка регистрации: ${result.message}", Toast.LENGTH_LONG).show()
                }
                // Если добавили ApiResult.Loading
                // is ApiResult.Loading -> { /* Можно обработать состояние загрузки здесь, если progressBar не достаточно */ }
                else -> {}
            }
        }

        // Наблюдаем за состоянием загрузки
        authViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarRegister.isVisible = isLoading
            binding.btnRegister.isEnabled = !isLoading
            binding.tvGoToLogin.isEnabled = !isLoading // Блокируем и ссылку на логин
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Очистка binding для предотвращения утечек памяти
    }
}
