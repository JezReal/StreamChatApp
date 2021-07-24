package com.plcoding.streamchatapp.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.plcoding.streamchatapp.R
import com.plcoding.streamchatapp.databinding.FragmentLoginBinding
import com.plcoding.streamchatapp.ui.BindingFragment
import com.plcoding.streamchatapp.ui.login.LoginViewModel.LoginEvent.*
import com.plcoding.streamchatapp.util.Constants
import com.plcoding.streamchatapp.util.Constants.MIN_USERNAME_LENGTH
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BindingFragment<FragmentLoginBinding>() {

    private val viewModel: LoginViewModel by viewModels()

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentLoginBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnConfirm.setOnClickListener {
            setUpConnectingUiState()
            viewModel.connectUser(binding.etUsername.text.toString())
        }

        binding.etUsername.addTextChangedListener {
            binding.etUsername.error = null
        }

        subscribeToEvents()
    }

    private fun subscribeToEvents() {
        lifecycleScope.launchWhenStarted {
            viewModel.loginEvent.collect { event ->
                when (event) {
                    is ErrorInputTooShort -> {
                        setUpIdleUiState()
                        binding.etUsername.error = getString(R.string.error_username_too_short, MIN_USERNAME_LENGTH)
                    }

                    is ErrorLogin -> {
                        setUpIdleUiState()

                        Snackbar.make(binding.root, event.message, Snackbar.LENGTH_LONG).show()
                    }

                    is Success -> {
                        setUpIdleUiState()
                        Snackbar.make(binding.root, "Login success", Snackbar.LENGTH_LONG).show()

                    }
                }

            }
        }
    }

    private fun setUpConnectingUiState() {
        binding.progressBar.isVisible = true
        binding.btnConfirm.isEnabled = false
    }

    private fun setUpIdleUiState() {
        binding.progressBar.isVisible = false
        binding.btnConfirm.isEnabled = true
    }
}