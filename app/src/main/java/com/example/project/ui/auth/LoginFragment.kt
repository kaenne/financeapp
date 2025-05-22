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
import com.example.project.databinding.FragmentLoginBinding
import com.example.project.utils.SessionManager

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        sessionManager = (requireActivity().application as WalletApp).sessionManager
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (sessionManager.isLoggedIn()) {
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            return
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmailLogin.text.toString().trim()
            val password = binding.etPasswordLogin.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            authViewModel.login(email, password)
        }

        binding.tvGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        // Наблюдаем за результатом входа
        authViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ApiResult.Success -> {
                    // Успешный вход
                    val authData = result.data
                    sessionManager.createLoginSession(authData.userId, authData.token, authData.email)
                    Toast.makeText(requireContext(), "Вход успешен!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }
                is ApiResult.Error -> {
                    // Ошибка входа
                    Toast.makeText(requireContext(), "Ошибка входа: ${result.message}", Toast.LENGTH_LONG).show()
                }
                // is ApiResult.Loading -> { /* ... */ }
                else -> {}
            }
        }

        // Наблюдаем за состоянием загрузки
        authViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarLogin.isVisible = isLoading // Убедись, что ID ProgressBar правильный
            binding.btnLogin.isEnabled = !isLoading
            binding.tvGoToRegister.isEnabled = !isLoading
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
