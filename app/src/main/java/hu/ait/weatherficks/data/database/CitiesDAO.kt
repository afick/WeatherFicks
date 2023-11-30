package hu.ait.weatherficks.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CitiesDAO {
    @Query("SELECT * FROM cities ORDER BY city ASC")
    fun getAllCities(): Flow<List<CityItem>>

    @Query("SELECT * FROM cities WHERE id = :cityId")
    fun getCityById(cityId: Int): CityItem

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCity(city: CityItem)

    @Update
    suspend fun updateCity(city: CityItem)

    @Delete
    suspend fun deleteCity(city: CityItem)

    @Query("DELETE FROM cities")
    suspend fun deleteAllCities()
}