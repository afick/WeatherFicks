package hu.ait.weatherficks.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Update version if changing the structure of the table
@Database(entities = [CityItem::class], version = 2, exportSchema = false)
abstract class CitiesDatabase : RoomDatabase() {

    abstract fun citiesDao(): CitiesDAO

    companion object {
        @Volatile
        private var Instance: CitiesDatabase? = null

        fun getDatabase(context: Context): CitiesDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, CitiesDatabase::class.java,
                    "cities_database.db")
                    // Setting this option in your app's database builder means that Room
                    // permanently deletes all data from the tables in your database when it
                    // attempts to perform a migration with no defined migration path.
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}