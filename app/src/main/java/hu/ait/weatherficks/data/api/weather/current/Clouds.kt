package hu.ait.weatherficks.data.api.weather.current


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Clouds(
    @SerialName("all")
    var all: Int? = null
)