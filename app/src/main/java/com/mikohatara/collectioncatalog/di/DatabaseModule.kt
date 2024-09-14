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

    /*  I wrote this before realizing I hadn't applied AutoMigration to the Database,
    *   so I didn't actually use this, but let it lay here as a reminder.
    * */
    private val MIGRATION_7_9 = object : Migration(8, 9) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `archive` ( " +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`country` TEXT NOT NULL, " +
                        "`region_1st` TEXT, `region_2nd` TEXT, `region_3rd` TEXT, " +
                        "`type` TEXT NOT NULL, " +
                        "`period_start` INTEGER, `period_end` INTEGER, `year` INTEGER, " +
                        "`reg_no` TEXT NOT NULL, " +
                        "`image_path` TEXT, `notes` TEXT, `vehicle` TEXT, `date` TEXT, " +
                        "`cost` INTEGER, `value` INTEGER, `status` TEXT, " +
                        "`width` INTEGER, `height` INTEGER, `weight` REAL, " +
                        "`color_main` TEXT, `color_secondary` TEXT, " +
                        "`source_name` TEXT, `source_alias` TEXT, `source_type` TEXT, " +
                        "`source_details` TEXT, `source_country` TEXT, " +
                        "`archival_date` TEXT, `recipient_name` TEXT, `recipient_alias` TEXT, " +
                        "`archival_reason` TEXT, `archival_details` TEXT, " +
                        "`price` INTEGER, `recipient_country` TEXT)"
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
