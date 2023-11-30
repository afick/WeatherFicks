package hu.ait.weatherficks.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "cities")
data class CityItem(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "city") var city: String,
    @ColumnInfo(name = "country") var country: String,
    @ColumnInfo(name = "state") var state: String?
) : Serializable