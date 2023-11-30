package hu.ait.weatherficks.ui.screen.weather

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.ait.weatherficks.data.api.weather.current.WeatherResult
import hu.ait.weatherficks.data.api.weather.forecast.ForecastResult
import hu.ait.weatherficks.network.CurrentWeatherAPI
import hu.ait.weatherficks.network.WeatherForecastAPI
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherForecastAPI: WeatherForecastAPI,
    private val currentWeatherAPI: CurrentWeatherAPI
) : ViewModel() {
    private val envVar: String = System.getenv("weather_api_key") ?: "a19b4b624dfc08fd6eef312e30126126"
    var weatherUiState: WeatherUiState by mutableStateOf(WeatherUiState.Init)

    fun getWeather(
        location: String = "Budapest,hu",
        units: String = "imperial"
    ) {
        weatherUiState = WeatherUiState.Loading
        viewModelScope.launch {
            weatherUiState = try {
                val weatherResult = currentWeatherAPI.getCurrentWeather(location, units, envVar)
                val forecastResult = weatherForecastAPI.getWeatherForecast(location, units, envVar)
                Log.d("ui state", "done")
                WeatherUiState.Success(weatherResult, forecastResult)
            } catch (e: Exception) {
                WeatherUiState.Error(e.message ?: "Unknown error")
            }
        }

    }
}

sealed interface WeatherUiState {
    object Init : WeatherUiState
    object Loading : WeatherUiState
    data class Success(val weatherResult: WeatherResult, val forecastResult: ForecastResult) :
        WeatherUiState
    data class Error(val errorMsg: String) : WeatherUiState
}