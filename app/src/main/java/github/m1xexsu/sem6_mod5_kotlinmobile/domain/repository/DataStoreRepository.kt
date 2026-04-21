package github.m1xexsu.sem6_mod5_kotlinmobile.domain.repository

interface DataStoreRepository {
    suspend fun getData(): Boolean
    suspend fun setData(_i: Boolean)
}