package com.andrzejbrzezinski.rossmannproductlist.filmpackage.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.dataclasses.Films
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.dataclasses.Users
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.interfaces.FilmDao
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.interfaces.UserDao

@Database(entities = [Films::class, Users::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun filmDao(): FilmDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        /*val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS users (password TEXT,userid INTEGER PRIMARY KEY NOT NULL,liked TEXT, username TEXT  )"




                                   )
                //database.execSQL("DROP TABLE IF EXISTS users")
                //database.execSQL("ALTER TABLE users_new RENAME TO users")
            }}*/
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE films ADD COLUMN owner TEXT")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val tempInstance = INSTANCE
                if (tempInstance != null) {
                    return tempInstance
                }
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "database-films"
                ).addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
