package ec.edu.uce.book.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.edu.uce.book.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _loginResult = MutableStateFlow<Boolean?>(null)
    val loginResult: StateFlow<Boolean?> = _loginResult

    private val _registerResult = MutableStateFlow<Boolean?>(null)
    val registerResult: StateFlow<Boolean?> = _registerResult

    fun login(name: String, lastName: String, password: String) {
        viewModelScope.launch {
            val result = repository.login(name, lastName, password)
            _loginResult.value = result
        }
    }

    fun register(name: String, lastName: String, password: String) {
        viewModelScope.launch {
            val result = repository.registerUser(name, lastName, password)
            _registerResult.value = result
        }
    }

    fun clearLoginResult() {
        _loginResult.value = null
    }

    fun clearRegisterResult() {
        _registerResult.value = null
    }
}