package ec.edu.uce.book.data.repository

import ec.edu.uce.book.core.network.ApiService
import ec.edu.uce.book.data.dao.LastLoginDao
import ec.edu.uce.book.data.entity.LastLoginEntity
import kotlinx.coroutines.flow.Flow

class LastLoginRepository(
        private val api: ApiService,
        private val dao: LastLoginDao
) {
    fun observeLastLogin(): Flow<LastLoginEntity?> = dao.observe()

    suspend fun fetchAndSave() {
        val resp = api.getCurrentTime()
        dao.upsert(LastLoginEntity(id = 1, currentTime = resp.currentTime))
    }
}
