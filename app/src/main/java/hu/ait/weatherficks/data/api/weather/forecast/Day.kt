package hu.ait.weatherficks.data.api.weather.forecast


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Day(
    @SerialName("clouds")
    var clouds: Clouds? = null,
    @SerialName("dt")
    var dt: Int? = null,
    @SerialName("dt_txt")
    var dtTxt: String? = null,
    @SerialName("main")
    var main: Main? = null,
    @SerialName("pop")
    var pop: Double? = null,
    @SerialName("rain")
    var rain: Rain? = null,
    @SerialName("sys")
    var sys: Sys? = null,
    @SerialName("visibility")
    var visibility: Int? = null,
    @SerialName("weather")
    var weather: List<Weather?>? = null,
    @SerialName("wind")
    var wind: Wind? = null
)