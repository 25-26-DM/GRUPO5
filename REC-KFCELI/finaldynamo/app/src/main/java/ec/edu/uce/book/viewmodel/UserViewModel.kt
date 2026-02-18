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

    private val _codeSentResult = MutableStateFlow<Boolean?>(null)
    val codeSentResult: StateFlow<Boolean?> = _codeSentResult

    private val _verificationResult = MutableStateFlow<Boolean?>(null)
    val verificationResult: StateFlow<Boolean?> = _verificationResult

    private var temporaryCode: String? = null

    fun sendLoginCode(email: String) {
        if (email != "grupo5ucedm@outlook.com") {
            _codeSentResult.value = false
            return
        }

        viewModelScope.launch {
            val code = (100000..999999).random().toString()
            temporaryCode = code
            val success = repository.sendLoginCode(email, code)
            _codeSentResult.value = success
        }
    }

    fun verifyLoginCode(code: String) {
        _verificationResult.value = code == temporaryCode
    }

    // Métodos antiguos mantenidos por si son necesarios
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
    fun clearCodeSentResult(){
        _codeSentResult.value=null
    }
    fun clearVerificationResult(){
        _verificationResult.value=null
    }
}