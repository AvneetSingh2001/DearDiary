package com.avicodes.deardiary.di

import android.content.Context
import androidx.room.Room
import com.avicodes.mongo.database.ImagesDatabase
import com.avicodes.util.Constants.IMAGES_DATABASE
import com.avicodes.util.connectivity.NetworkConnectivityObserver
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
    fun provideDatabase(
        @ApplicationContext context: Context
    ): ImagesDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = ImagesDatabase::class.java,
            name = IMAGES_DATABASE
        ).build()
    }

    @Singleton
    @Provides
    fun provideImageToUploadDao(database: ImagesDatabase) = database.imageToUploadDao()

    @Singleton
    @Provides
    fun provideImageToDeleteDao(database: ImagesDatabase) = database.imageToDeleteDao()

    @Singleton
    @Provides
    fun provideNetworkConnectivityObserver(
        @ApplicationContext context: Context
    ) = NetworkConnectivityObserver(context = context)
}