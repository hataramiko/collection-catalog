package com.mikohatara.collectioncatalog.di

import android.content.Context
import androidx.room.Room
import com.mikohatara.collectioncatalog.data.CollectionDatabase
import com.mikohatara.collectioncatalog.data.PlateDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    fun providePlateDao(collectionDatabase: CollectionDatabase): PlateDao {
        return collectionDatabase.plateDao()
    }

    @Provides
    @Singleton
    fun provideCollectionDatabase(@ApplicationContext appContext: Context): CollectionDatabase {
        return Room.databaseBuilder(
            appContext,
            CollectionDatabase::class.java,
            "Plate"
        )
            .fallbackToDestructiveMigration() // TODO work out migration
            .build()
    }
}
