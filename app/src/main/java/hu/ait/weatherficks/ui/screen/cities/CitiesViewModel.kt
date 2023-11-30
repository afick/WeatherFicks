package hu.ait.weatherficks.ui.screen.cities

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.ait.mapsdemo.location.LocationManager
import hu.ait.weatherficks.data.database.CitiesDAO
import hu.ait.weatherficks.data.database.CityItem
import hu.ait.weatherficks.network.CurrentWeatherAPI
import hu.ait.weatherficks.network.LocationAPI
import hu.ait.weatherficks.network.VerifierAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CitiesViewModel @Inject constructor(
    private val citiesDAO: CitiesDAO,
    private val locationManager: LocationManager,
    private val currentWeatherAPI: CurrentWeatherAPI,
    private val verifierAPI: VerifierAPI,
    private val locationAPI: LocationAPI
) : ViewModel() {
    private val envVar: String = System.getenv("weather_api_key") ?: "a19b4b624dfc08fd6eef312e30126126"

    private var currentCity = mutableStateOf("Loading...")
    private var currentCountry = mutableStateOf("")

    fun getAllCities(): Flow<List<CityItem>> {
        return citiesDAO.getAllCities()
    }

    fun addCity(city: CityItem) {
        viewModelScope.launch {
            citiesDAO.insertCity(city)
        }
    }

    fun removeCity(city: CityItem) {
        viewModelScope.launch {
            citiesDAO.deleteCity(city)
        }
    }

    fun deleteAllCities() {
        viewModelScope.launch {
            citiesDAO.deleteAllCities()
        }
    }

    fun startLocationMonitoring() {
        viewModelScope.launch {
            locationManager
                .fetchUpdates()
                .collect {
                    Log.d("CHECK", "Location: ${it.latitude}, ${it.longitude}")
                    try {
                        val locationResultItem = locationAPI.getLocation(
                            it.latitude.toString(),
                            it.longitude.toString(),
                            0,
                            envVar
                        )[0]
                        currentCity.value = locationResultItem.name.toString()
                        currentCountry.value = locationResultItem.country.toString()
                    } catch (e: Exception) {
                        currentCity.value = "Unknown"
                        currentCountry.value = "Unknown"
                    }

                    this.coroutineContext.job.cancel()
                }
        }
    }

    suspend fun verifyCity(city: String, state: String, country: String): Boolean {
        val location = if (state != "") {
            "${city},${state},${country}"
        } else {
            "${city},${country}"
        }

        var valid = true
        try {
            val result = verifierAPI.verifyLocation(location, 0, envVar)
            if (result.isEmpty()) {
                valid = false
            }
        } catch (e: Exception) {
            valid = false
        }

        return valid
    }

    fun getCity(): String {
        return currentCity.value
    }

    fun getCountry(): String {
        return currentCountry.value
    }

    suspend fun getCurrentTempIcon(
        city: CityItem,
        units: String = "imperial"
    ): Pair<Double, String> {
        var temp = 0.0
        var icon = "0"
        try {
            val location = if (city.state != "") {
                "${city.city},${city.state},${city.country}"
            } else {
                "${city.city},${city.country}"
            }

            val weatherResult = currentWeatherAPI.getCurrentWeather(location, units, envVar)
            temp = weatherResult.main?.temp ?: 0.0
            icon = weatherResult.weather?.get(0)?.icon ?: ""

        } catch (e: Exception) {
            Log.d("getCurrentTempIcon", "${e.message}")
        }
        return Pair(temp, icon)
    }
}