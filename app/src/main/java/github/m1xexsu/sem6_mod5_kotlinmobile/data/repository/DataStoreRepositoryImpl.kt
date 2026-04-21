package github.m1xexsu.sem6_mod5_kotlinmobile.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import github.m1xexsu.sem6_mod5_kotlinmobile.data.preferences.Datastore
import github.m1xexsu.sem6_mod5_kotlinmobile.data.preferences.dataStore
import github.m1xexsu.sem6_mod5_kotlinmobile.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DataStoreRepositoryImpl(
    private val context: Context
): DataStoreRepository {
    override suspend fun getData(): Boolean {
        return context.dataStore.data.map { it -> it[Datastore.IS_COLORED] ?: false }.first()
    }

    override suspend fun setData(_i: Boolean) {
        context.dataStore.edit { it[Datastore.IS_COLORED] = _i }
    }
}