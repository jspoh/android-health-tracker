package com.example.fittrack.di

import com.example.fittrack.data.repository.ActivityRepositoryImpl
import com.example.fittrack.data.repository.StepsRepositoryImpl
import com.example.fittrack.data.repository.UserRepositoryImpl
import com.example.fittrack.domain.repository.ActivityRepository
import com.example.fittrack.domain.repository.StepsRepository
import com.example.fittrack.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindActivityRepository(impl: ActivityRepositoryImpl): ActivityRepository

    @Binds
    @Singleton
    abstract fun bindStepsRepository(impl: StepsRepositoryImpl): StepsRepository
}
