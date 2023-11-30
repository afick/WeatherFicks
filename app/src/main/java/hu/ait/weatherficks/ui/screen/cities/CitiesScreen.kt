package hu.ait.weatherficks.ui.screen.cities

import android.Manifest
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import hu.ait.weatherficks.R
import hu.ait.weatherficks.data.database.CityItem
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CitiesScreen(
    citiesViewModel: CitiesViewModel = hiltViewModel(),
    onNavigateToWeather: (String, String?, String, String) -> Unit
) {
    val context = LocalContext.current
    var showAddCityDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val cityList by citiesViewModel.getAllCities()
        .collectAsState(initial = emptyList())


    var units by rememberSaveable {
        mutableStateOf(context.getString(R.string.imperial_unit))
    }

    var imperial by rememberSaveable {
        mutableStateOf(true)
    }

    val fineLocationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )


    if (!fineLocationPermissionState.status.isGranted) {
        LaunchedEffect(Unit) {
            fineLocationPermissionState.launchPermissionRequest()
        }
    } else {
        LaunchedEffect(Unit) {
            citiesViewModel.startLocationMonitoring()
        }
    }

    val currentCity = citiesViewModel.getCity()
    val currentCountry = citiesViewModel.getCountry()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name), fontWeight = FontWeight.Medium) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                actions = {
                    Text(text = units, modifier = Modifier.padding(horizontal = 4.dp))
                    Switch(checked = imperial, onCheckedChange = {
                        imperial = it
                        units = if (imperial) {
                            context.getString(R.string.imperial_unit)
                        } else {
                            context.getString(R.string.metric_unit)
                        }
                    })
                    IconButton(onClick = { citiesViewModel.deleteAllCities() }) {
                        Icon(Icons.Outlined.Delete, null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showAddCityDialog = true
                },
                containerColor = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(16.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add",
                    tint = Color.White,
                )
            }
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            if (showAddCityDialog) {
                AddNewCityForm(
                    citiesViewModel,
                    cityList
                ) {
                    showAddCityDialog = false
                }
            }
            CityCard(
                CityItem(0, currentCity, currentCountry, stringResource(R.string.empty_str)),
                {},
                false,
                onNavigateToWeather,
                units
            )
            LazyColumn {
                items(cityList) { city ->
                    CityCard(
                        cityItem = city,
                        onRemoveCity = { citiesViewModel.removeCity(city) },
                        onNavigateToWeather = onNavigateToWeather,
                        units = units
                    )
                }
            }
        }

    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityCard(
    cityItem: CityItem,
    onRemoveCity: () -> Unit = {},
    showDelete: Boolean = true,
    onNavigateToWeather: (String, String?, String, String) -> Unit,
    units: String,
    citiesViewModel: CitiesViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    var temp by rememberSaveable {
        mutableDoubleStateOf(0.0)
    }
    var icon by rememberSaveable {
        mutableStateOf(context.getString(R.string.default_icon_txt))
    }
    var degree by rememberSaveable {
        mutableStateOf(context.getString(R.string.deg_f))
    }
    SideEffect {
        coroutineScope.launch {
            val (a, b) = citiesViewModel.getCurrentTempIcon(cityItem, units)
            temp = a
            icon = b
            degree = if (units == context.getString(R.string.imperial_unit))
                context.getString(R.string.deg_f) else context.getString(R.string.deg_c)
        }
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        modifier = Modifier.padding(8.dp),
        onClick = {
            if (cityItem.city != context.getString(R.string.loading_text)) {
                // Create empty string for navigation if state is empty
                val state = if (cityItem.state != context.getString(R.string.empty_str)) cityItem.state ?: context.getString(R.string.space_string) else context.getString(R.string.space_string)
                onNavigateToWeather(cityItem.city, state, cityItem.country, units)
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .animateContentSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val cardLoc =
                    if (cityItem.state == "") stringResource(
                        R.string.city_country_formatted,
                        cityItem.city,
                        cityItem.country
                    ) else stringResource(R.string.city_state_country_spaced_str, cityItem.city, cityItem.state!!, cityItem.country)
                Text(
                    text = cardLoc,
                    fontSize = 20.sp, fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.weight(1.0f))
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(
                            "https://openweathermap.org/img/w/${
                                icon
                            }.png"
                        ).crossfade(true)
                        .build(),
                    contentDescription = "Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(75.dp)
                        .clip(CircleShape)
                )
                if (cityItem.country.isNotEmpty()) {
                    Text(
                        text = stringResource(id = R.string.current_temp_string, temp.toInt(), degree),
                        modifier = Modifier.padding(4.dp),
                        fontWeight = FontWeight.Medium, fontSize = 20.sp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.black_temp, degree), modifier = Modifier.padding(4.dp),
                        fontWeight = FontWeight.Medium, fontSize = 20.sp
                    )
                }
                if (showDelete) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete City",
                        modifier = Modifier.clickable {
                            onRemoveCity()
                        },
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
private fun AddNewCityForm(
    citiesViewModel: CitiesViewModel = hiltViewModel(),
    cityList: List<CityItem>,
    onDialogDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    val citySet = cityList.map { Triple(it.city, it.state, it.country) }.toSet()
    Dialog(onDismissRequest = onDialogDismiss) {
        var cityName by rememberSaveable {
            mutableStateOf(context.getString(R.string.empty_str))
        }

        var countryAbb by rememberSaveable {
            mutableStateOf(context.getString(R.string.empty_str))
        }

        var stateAbb by rememberSaveable {
            mutableStateOf(context.getString(R.string.empty_str))
        }

        var cityErrorState by rememberSaveable {
            mutableStateOf(false)
        }

        var cityErrorText by rememberSaveable {
            mutableStateOf(context.getString(R.string.empty_str))
        }

        var countryErrorState by rememberSaveable {
            mutableStateOf(false)
        }

        var countryErrorText by rememberSaveable {
            mutableStateOf(context.getString(R.string.empty_str))
        }

        var stateErrorState by rememberSaveable {
            mutableStateOf(false)
        }

        var stateErrorText by rememberSaveable {
            mutableStateOf(context.getString(R.string.empty_str))
        }

        var overallErrorText by rememberSaveable {
            mutableStateOf(context.getString(R.string.empty_str))
        }

        val coroutineScope = rememberCoroutineScope()

        fun validate(text: String): Pair<Boolean, String> {
            if (text.isEmpty()) {
                return Pair(true, context.getString(R.string.cannot_be_empty_txt))
            }
            if (text.contains(Regex("[^a-zA-Z' ']"))) {
                return Pair(true, context.getString(R.string.only_letters_allowed))
            }
            return Pair(false, context.getString(R.string.empty_str))
        }

        fun validateCity(text: String) {
            val (a, b) = validate(text)
            cityErrorState = a
            cityErrorText = b
        }

        fun validateCountry(text: String) {
            if (text.length > 2) {
                countryErrorState = true
                countryErrorText = context.getString(R.string.only_2_letters_allowed)
                return
            }
            val (a, b) = validate(text)
            countryErrorState = a
            countryErrorText = b
        }

        fun validateState(text: String) {
            if (text.length > 2) {
                stateErrorState = true
                stateErrorText = context.getString(R.string.only_2_letters_allowed)
                return
            }
            if (text.contains(Regex("[^a-zA-Z]"))) {
                stateErrorState = true
                stateErrorText = context.getString(R.string.only_letters_allowed)
                return
            }
            stateErrorState = false
            stateErrorText = context.getString(R.string.empty_str)
        }

        fun String.capitalizeWords(delimiter: String = context.getString(R.string.space_string)) =
            split(delimiter).joinToString(delimiter) { word ->

                val smallCaseWord = word.lowercase()
                smallCaseWord.replaceFirstChar(Char::titlecaseChar)

            }

        Column(
            Modifier
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = cityName,
                onValueChange = { cityName = it.capitalizeWords(); validateCity(cityName) },
                label = { Text(text = stringResource(R.string.city_name_label)) },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (cityErrorState)
                        Icon(
                            Icons.Filled.Warning,
                            "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                },
                supportingText = {
                    if (cityErrorState) {
                        Text(
                            text = cityErrorText,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (cityErrorState) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = if (cityErrorState) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                )
            )

            OutlinedTextField(
                value = countryAbb,
                onValueChange = {
                    if (it.length <= 2) {
                        countryAbb = it.uppercase()
                    }

                    validateCountry(countryAbb)
                },
                label = { Text(text = stringResource(R.string.country_abbreviation_label)) },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (countryErrorState)
                        Icon(
                            Icons.Filled.Warning,
                            "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                },
                supportingText = {
                    if (countryErrorState) {
                        Text(
                            text = countryErrorText,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (countryErrorState) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = if (countryErrorState) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                )
            )

            OutlinedTextField(
                value = stateAbb,
                onValueChange = {
                    if (it.length <= 2) {
                        stateAbb = it.uppercase(Locale.getDefault())
                    }
                    validateState(stateAbb)
                },
                label = { Text(text = stringResource(R.string.state_abbreviation_label)) },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (stateErrorState)
                        Icon(
                            Icons.Filled.Warning,
                            "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                },
                supportingText = {
                    if (stateErrorState) {
                        Text(
                            text = stringResource(R.string.state_txt),
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (stateErrorState) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = if (stateErrorState) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                )
            )
            if (overallErrorText.isNotEmpty()) {
                Text(text = overallErrorText, color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 10.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    val ready = cityName.isNotEmpty() &&
                            !cityErrorState && countryAbb.isNotEmpty() &&
                            !countryErrorState && !stateErrorState

                    if (ready) {
                        if (citySet.contains(Triple(cityName, stateAbb, countryAbb))) {
                            overallErrorText = context.getString(R.string.already_added_txt, cityName)
                        } else {
                            coroutineScope.launch {
                                val valid =
                                    citiesViewModel.verifyCity(cityName, stateAbb, countryAbb)
                                if (!valid) {
                                    overallErrorText =
                                        context.getString(R.string.city_not_found_error, cityName)
                                    return@launch
                                } else {
                                    overallErrorText = context.getString(R.string.empty_str)
                                    citiesViewModel.addCity(
                                        CityItem(
                                            0,
                                            cityName,
                                            countryAbb,
                                            stateAbb
                                        )
                                    )
                                    onDialogDismiss()
                                }
                            }
                        }
                    } else {
                        if (cityName.isEmpty()) {
                            cityErrorState = true
                            cityErrorText = context.getString(R.string.cannot_be_empty)
                        }
                        if (countryAbb.isEmpty()) {
                            countryErrorState = true
                            countryErrorText = context.getString(R.string.cannot_be_empty)
                        }
                    }
                }
                ) {
                    Text(text = stringResource(R.string.add_city))
                }
            }
        }
    }
}