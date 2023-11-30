package hu.ait.weatherficks.data.api.weather.location


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocalNames(
    @SerialName("be")
    var be: String? = null,
    @SerialName("cy")
    var cy: String? = null,
    @SerialName("en")
    var en: String? = null,
    @SerialName("fr")
    var fr: String? = null,
    @SerialName("he")
    var he: String? = null,
    @SerialName("ko")
    var ko: String? = null,
    @SerialName("mk")
    var mk: String? = null,
    @SerialName("ru")
    var ru: String? = null
)