package com.example.calculator.framework.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext


@Dao
interface CalculationDatabaseDAO {

    @Insert
    fun insert(calculation: Calculation)

    @Query("SELECT * from calculation_history_table WHERE id = :key")
    fun get(key: Long): Calculation?

    @Query("SELECT * FROM calculation_history_table ORDER BY datetime(time_calculated)")
    fun getAllCalculations(): LiveData<List<Calculation>>

    @Query("DELETE FROM calculation_history_table")
    fun clear()
}


@Database(entities = [Calculation::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class CalculationDatabase : RoomDatabase() {

    abstract val calculationDatabaseDAO: CalculationDatabaseDAO

    companion object {

        private lateinit var INSTANCE: CalculationDatabase

        fun getInstance(context: Context): CalculationDatabase {
            // Handle multiple calls to this function one thread at a time
            synchronized(this) {

                // If instance is uninitialized, build a new database instance
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        CalculationDatabase::class.java,
                        "calculation_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }

                return INSTANCE
            }
        }
    }
}
@Module
@InstallIn(ActivityRetainedComponent::class)
object CalculationDatabaseModule {
    @Provides
    fun provideCalculationDatabaseDAO(
        @ApplicationContext context: Context
    ): CalculationDatabaseDAO {
        return CalculationDatabase.getInstance(context).calculationDatabaseDAO
    }
}
