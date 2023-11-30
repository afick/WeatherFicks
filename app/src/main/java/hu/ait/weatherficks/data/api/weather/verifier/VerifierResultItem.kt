package hu.ait.weatherficks.data.api.weather.verifier


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifierResultItem(
    @SerialName("country")
    var country: String? = null,
    @SerialName("lat")
    var lat: Double? = null,
    @SerialName("local_names")
    var localNames: LocalNames? = null,
    @SerialName("lon")
    var lon: Double? = null,
    @SerialName("name")
    var name: String? = null,
    @SerialName("state")
    var state: String? = null
)