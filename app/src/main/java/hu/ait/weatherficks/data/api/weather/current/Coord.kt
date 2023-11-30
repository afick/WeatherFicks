package hu.ait.weatherficks.data.api.weather.current


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Coord(
    @SerialName("lat")
    var lat: Double? = null,
    @SerialName("lon")
    var lon: Double? = null
)