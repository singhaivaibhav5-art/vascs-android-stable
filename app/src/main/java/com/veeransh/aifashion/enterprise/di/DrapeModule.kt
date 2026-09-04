package com.veeransh.aifashion.enterprise.di

import com.veeransh.aifashion.enterprise.data.repository.DrapeRepository
import com.veeransh.aifashion.enterprise.data.repository.DrapeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DrapeModule {
    @Binds
    @Singleton
    abstract fun bindDrapeRepository(impl: DrapeRepositoryImpl): DrapeRepository
}
