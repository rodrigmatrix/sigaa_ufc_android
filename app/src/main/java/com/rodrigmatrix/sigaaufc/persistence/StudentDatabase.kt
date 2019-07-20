package com.rodrigmatrix.sigaaufc.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rodrigmatrix.sigaaufc.persistence.entity.*

@Database(
    entities = [Student::class, RuCard::class, StudentClass::class, HistoryRU::class, News::class],
    version = 1,
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

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                StudentDatabase::class.java,
                "sigaa.db")
                .build()
    }
}
