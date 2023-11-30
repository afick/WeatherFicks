package hu.ait.weatherficks.network

import hu.ait.weatherficks.data.api.weather.current.WeatherResult
import hu.ait.weatherficks.data.api.weather.forecast.ForecastResult
import hu.ait.weatherficks.data.api.weather.location.LocationResultItem
import hu.ait.weatherficks.data.api.weather.verifier.VerifierResultItem
import retrofit2.http.GET
import retrofit2.http.Query


// FULL URL: https://api.openweathermap.org/data/2.5/forecast?q=Budapest,hu&units=imperial&appid=a19b4b624dfc08fd6eef312e30126126
// Host: https://api.openweathermap.org
// Path: /data/2.5/forecast
// Query Params:
    // ?q=Budapest,hu&
    // units=imperial&
    // appid=a19b4b624dfc08fd6eef312e30126126


interface CurrentWeatherAPI {
    @GET("/data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("q") location: String,
        @Query("units") units: String,
        @Query("appid") accessKey: String
    ): WeatherResult

    @GET("/data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("units") units: String,
        @Query("appid") accessKey: String
    ): WeatherResult
}

interface WeatherForecastAPI {
    @GET("/data/2.5/forecast")
    suspend fun getWeatherForecast(
        @Query("q") location: String,
        @Query("units") units: String,
        @Query("appid") accessKey: String
    ): ForecastResult
}

interface LocationAPI {
    @GET("/geo/1.0/reverse")
    suspend fun getLocation(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("limit") limit: Int,
        @Query("appid") accessKey: String
    ): ArrayList<LocationResultItem>
}

interface VerifierAPI {
    @GET("/geo/1.0/direct")
    suspend fun verifyLocation(
        @Query("q") location: String,
        @Query("limit") limit: Int,
        @Query("appid") accessKey: String
    ): ArrayList<VerifierResultItem>
}
