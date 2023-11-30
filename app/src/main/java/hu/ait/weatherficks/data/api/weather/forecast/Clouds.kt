package hu.ait.weatherficks.data.api.weather.forecast


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Clouds(
    @SerialName("all")
    var all: Int? = null
)