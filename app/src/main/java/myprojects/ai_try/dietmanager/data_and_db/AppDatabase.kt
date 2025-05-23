package myprojects.ai_try.dietmanager.data_and_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import myprojects.ai_try.dietmanager.my_entity.DietPlanEntity

@Database(entities = [DietPlanEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dietPlanDao(): DietPlanDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "diet_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

