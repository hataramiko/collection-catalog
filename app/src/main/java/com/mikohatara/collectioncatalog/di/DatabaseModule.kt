package com.mikohatara.collectioncatalog.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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

    private val MIGRATION_7_8 = object : Migration(7, 8) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `wishlist` ( " +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`reg_no` TEXT, `image_path` TEXT, `notes` TEXT, " +
                        "`country` TEXT NOT NULL, " +
                        "`region_1st` TEXT, `region_2nd` TEXT, `region_3rd` TEXT, " +
                        "`type` TEXT NOT NULL, " +
                        "`period_start` INTEGER, `period_end` INTEGER, `year` INTEGER, " +
                        "`color_main` TEXT, `color_secondary` TEXT)"
            )
        }
    }

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
            .addMigrations(MIGRATION_7_8)
            .fallbackToDestructiveMigration()
            .build()
    }
}
