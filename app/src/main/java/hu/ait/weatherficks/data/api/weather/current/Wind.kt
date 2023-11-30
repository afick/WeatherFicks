package hu.ait.weatherficks.data.api.weather.current


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Wind(
    @SerialName("deg")
    var deg: Int? = null,
    @SerialName("speed")
    var speed: Double? = null
)