package hu.ait.weatherficks.data.api.weather.forecast


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class City(
    @SerialName("coord")
    var coord: Coord? = null,
    @SerialName("country")
    var country: String? = null,
    @SerialName("id")
    var id: Int? = null,
    @SerialName("name")
    var name: String? = null,
    @SerialName("population")
    var population: Int? = null,
    @SerialName("sunrise")
    var sunrise: Int? = null,
    @SerialName("sunset")
    var sunset: Int? = null,
    @SerialName("timezone")
    var timezone: Int? = null
)