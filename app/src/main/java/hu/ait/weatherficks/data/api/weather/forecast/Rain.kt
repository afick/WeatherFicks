package hu.ait.weatherficks.data.api.weather.forecast


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Rain(
    @SerialName("3h")
    var h: Double? = null
)