package hu.ait.weatherficks.data.api.weather.forecast


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForecastResult(
    @SerialName("city")
    var city: City? = null,
    @SerialName("cnt")
    var cnt: Int? = null,
    @SerialName("cod")
    var cod: String? = null,
    @SerialName("list")
    var list: List<Day>? = null,
    @SerialName("message")
    var message: Int? = null
)