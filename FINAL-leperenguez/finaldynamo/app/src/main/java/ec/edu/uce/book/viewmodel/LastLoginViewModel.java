package ec.edu.uce.book.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.edu.uce.book.data.repository.LastLoginRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LastLoginViewModel(
        private val repo: LastLoginRepository
) : ViewModel() {

    val lastLoginText: StateFlow<String> =
    repo.observeLastLogin()
            .map { it?.currentTime ?: "—" }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "—")

    fun refreshFromServerAndSave() {
        viewModelScope.launch {
            repo.fetchAndSave()
        }
    }
}
