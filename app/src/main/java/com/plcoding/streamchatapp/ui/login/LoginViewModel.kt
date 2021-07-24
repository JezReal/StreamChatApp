package com.plcoding.streamchatapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.streamchatapp.ui.login.LoginViewModel.LoginEvent.*
import com.plcoding.streamchatapp.util.Constants.MIN_USERNAME_LENGTH
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.call.await
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val client: ChatClient
) : ViewModel() {

    private val _loginEvent = MutableSharedFlow<LoginEvent>()
    val loginEvent = _loginEvent.asSharedFlow()

    private fun isValidUsername(username: String): Boolean {
        return username.length >= MIN_USERNAME_LENGTH
    }

    fun connectUser(username: String) {
        val trimmedUsername = username.trim()

        viewModelScope.launch {
            if (isValidUsername(trimmedUsername)) {
                val result = client.connectGuestUser(
                    userId = trimmedUsername,
                    username = trimmedUsername
                ).await()

                if (result.isError) {
                    _loginEvent.emit(ErrorLogin(result.error().message ?: "Unknown error"))
                    return@launch
                }

                _loginEvent.emit(Success)
            } else {
                _loginEvent.emit(ErrorInputTooShort)
            }
        }
    }

    sealed class LoginEvent {
        object ErrorInputTooShort : LoginEvent()
        data class ErrorLogin(val message: String) : LoginEvent()
        object Success : LoginEvent()
    }
}