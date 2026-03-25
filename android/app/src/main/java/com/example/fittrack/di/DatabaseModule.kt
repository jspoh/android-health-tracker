package com.example.fittrack.di

import android.content.Context
import androidx.room.Room
import com.example.fittrack.data.local.AppDatabase
import com.example.fittrack.data.local.dao.ActivityDao
import com.example.fittrack.data.local.dao.StepsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "fittrack.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideActivityDao(db: AppDatabase): ActivityDao = db.activityDao()

    @Provides
    fun provideStepsDao(db: AppDatabase): StepsDao = db.stepsDao()
}
