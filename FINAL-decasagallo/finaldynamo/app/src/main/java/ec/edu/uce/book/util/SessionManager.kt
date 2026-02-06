package ec.edu.uce.book.util

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object SessionManager {
    private const val MAX_SESSION_TIME = 15 * 60 * 1000L // 15 minutos en milisegundos
    private const val INACTIVITY_TIME = 5 * 60 * 1000L   // 5 minutos en milisegundos

    private var sessionStartTime: Long = 0
    private var lastActivityTime: Long = 0
    private var sessionJob: Job? = null

    private val _isSessionExpired = MutableStateFlow(false)
    val isSessionExpired: StateFlow<Boolean> = _isSessionExpired

    private val _sessionTimeRemaining = MutableStateFlow(0L)
    val sessionTimeRemaining: StateFlow<Long> = _sessionTimeRemaining

    /**
     * Inicia una nueva sesión
     */
    fun startSession() {
        sessionStartTime = System.currentTimeMillis()
        lastActivityTime = System.currentTimeMillis()
        _isSessionExpired.value = false

        sessionJob?.cancel()
        sessionJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                delay(1000) // Verificar cada segundo

                val currentTime = System.currentTimeMillis()
                val sessionDuration = currentTime - sessionStartTime
                val inactivityDuration = currentTime - lastActivityTime

                // Calcular tiempo restante
                val timeRemaining = MAX_SESSION_TIME - sessionDuration
                _sessionTimeRemaining.value = timeRemaining

                // Verificar si expiró por tiempo máximo (15 min)
                if (sessionDuration >= MAX_SESSION_TIME) {
                    _isSessionExpired.value = true
                    break
                }

                // Verificar si expiró por inactividad (5 min)
                if (inactivityDuration >= INACTIVITY_TIME) {
                    _isSessionExpired.value = true
                    break
                }
            }
        }
    }

    /**
     * Actualiza el tiempo de última actividad
     */
    fun updateActivity() {
        lastActivityTime = System.currentTimeMillis()
    }

    /**
     * Finaliza la sesión actual
     */
    fun endSession() {
        sessionJob?.cancel()
        _isSessionExpired.value = false
        _sessionTimeRemaining.value = 0L
    }

    /**
     * Obtiene el tiempo restante de sesión en minutos
     */
    fun getRemainingMinutes(): Int {
        val currentTime = System.currentTimeMillis()
        val sessionDuration = currentTime - sessionStartTime
        val remaining = MAX_SESSION_TIME - sessionDuration
        return (remaining / 60000).toInt().coerceAtLeast(0)
    }

    /**
     * Obtiene el tiempo de inactividad en minutos
     */
    fun getInactivityMinutes(): Int {
        val currentTime = System.currentTimeMillis()
        val inactivity = currentTime - lastActivityTime
        return (inactivity / 60000).toInt()
    }
}