package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.CourseDao
import com.example.data.dao.PlanDao
import com.example.data.dao.SelectedCourseDao
import com.example.data.entity.CourseEntity
import com.example.data.entity.PlanCourseEntity
import com.example.data.entity.PlanMetaEntity
import com.example.data.entity.SelectedCourseEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CourseEntity::class,
        SelectedCourseEntity::class,
        PlanCourseEntity::class,
        PlanMetaEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun selectedCourseDao(): SelectedCourseDao
    abstract fun planDao(): PlanDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "course_selection_database"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database)
                    }
                }
            }
        }

        suspend fun populateDatabase(database: AppDatabase) {
            val courseDao = database.courseDao()
            if (courseDao.getCourseCount() == 0) {
                courseDao.insertAll(CourseDataCatalog.initialCourses)
            }
            val planDao = database.planDao()
            for (p in 1..4) {
                planDao.insertPlanMeta(
                    PlanMetaEntity(
                        planNumber = p,
                        planName = "Plan $p",
                        savedAt = System.currentTimeMillis()
                    )
                )
            }
        }
    }
}
