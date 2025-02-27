package com.mikohatara.collectioncatalog.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mikohatara.collectioncatalog.data.CollectionDao
import com.mikohatara.collectioncatalog.data.PlateDao
import com.mikohatara.collectioncatalog.data.PlateDatabase
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
    private val MIGRATION_8_9 = object : Migration(8, 9) {
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

    private val MIGRATION_16_17 = object : Migration(16, 17) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `_new_collections` ( " +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`emoji` TEXT, `name` TEXT NOT NULL, `color` TEXT NOT NULL)"
            )
            db.execSQL(
                "INSERT INTO `_new_collections` (`id`, `emoji`, `name`, `color`) " +
                        "SELECT `id`, `emoji`, `name`, 'DEFAULT' FROM `collections`"
            )
            db.execSQL(
                "DROP TABLE `collections`"
            )
            db.execSQL(
                "ALTER TABLE `_new_collections` RENAME TO `collections`"
            )
        }
    }

    private val MIGRATION_17_18 = object : Migration(17, 18) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE `collections` ADD COLUMN `order` INTEGER NOT NULL DEFAULT 0"
            )
        }
    }

    @Provides
    fun providePlateDao(plateDatabase: PlateDatabase): PlateDao {
        return plateDatabase.plateDao()
    }

    @Provides
    fun provideCollectionDao(plateDatabase: PlateDatabase): CollectionDao {
        return plateDatabase.collectionDao()
    }

    @Provides
    @Singleton
    fun providePlateDatabase(@ApplicationContext appContext: Context): PlateDatabase {
        return Room.databaseBuilder(
            appContext,
            PlateDatabase::class.java,
            "Plate"
        )
            .addMigrations(MIGRATION_17_18)
            .fallbackToDestructiveMigration()
            .build()
    }
}
