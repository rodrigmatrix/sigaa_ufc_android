package com.rodrigmatrix.sigaaufc.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.impl.WorkDatabaseMigrations.MIGRATION_6_7
import com.rodrigmatrix.sigaaufc.persistence.entity.*

@Database(
    entities = [Student::class,
        RuCard::class,
        StudentClass::class,
        HistoryRU::class,
        News::class,
        JavaxFaces::class,
        Grade::class,
        Ira::class,
        File::class,
        Vinculo::class],
    version = 7,
    exportSchema = false
)

abstract class StudentDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao

    companion object{
        @Volatile private var instance: StudentDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE classes ADD COLUMN synced INTEGER NOT NULL default 0")
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                StudentDatabase::class.java,
                "sigaa.db")
                .addMigrations(MIGRATION_6_7)
                .fallbackToDestructiveMigration()
                .build()
    }
}
