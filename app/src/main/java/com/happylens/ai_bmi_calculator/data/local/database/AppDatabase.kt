package com.happylens.ai_bmi_calculator.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.happylens.ai_bmi_calculator.data.local.dao.BmiDao
import com.happylens.ai_bmi_calculator.data.local.dao.ProfileDao
import com.happylens.ai_bmi_calculator.data.local.entity.BmiEntity
import com.happylens.ai_bmi_calculator.data.local.entity.ChatMessageEntity
import com.happylens.ai_bmi_calculator.data.local.entity.ProfileEntity

@Database(entities = [BmiEntity::class, ChatMessageEntity::class, ProfileEntity::class], version = 7, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bmiDao(): BmiDao
    abstract fun profileDao(): ProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bmi_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
