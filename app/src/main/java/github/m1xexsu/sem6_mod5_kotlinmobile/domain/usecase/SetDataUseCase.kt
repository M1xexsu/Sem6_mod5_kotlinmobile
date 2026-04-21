package github.m1xexsu.sem6_mod5_kotlinmobile.domain.usecase

import github.m1xexsu.sem6_mod5_kotlinmobile.data.repository.DataStoreRepositoryImpl

class SetDataUseCase(private val repo: DataStoreRepositoryImpl) {
    suspend operator fun invoke (_i: Boolean): Unit = repo.setData(_i)
}