package hu.ait.weatherficks.ui.screen.weather

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.component.shape.shader.verticalGradient
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.composed.plus
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.composed.plus
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf
import hu.ait.weatherficks.R
import hu.ait.weatherficks.data.api.weather.current.WeatherResult
import hu.ait.weatherficks.data.api.weather.forecast.ForecastResult
import hu.ait.weatherficks.ui.screen.weather.WeatherUiState.Error
import hu.ait.weatherficks.ui.screen.weather.WeatherUiState.Init
import hu.ait.weatherficks.ui.screen.weather.WeatherUiState.Loading
import hu.ait.weatherficks.ui.screen.weather.WeatherUiState.Success
import java.util.Calendar
import java.util.Collections.max
import java.util.Collections.min
import java.util.Locale
import kotlin.math.roundToInt


@Composable
fun WeatherScreen(
    weatherViewModel: WeatherViewModel = hiltViewModel(),
    city: String,
    state: String?,
    country: String,
    units: String
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val location = if (state == context.getString(R.string.space_string)) context.getString(
            R.string.city_country_str,
            city,
            country
        ) else context.getString(R.string.city_state_country_str, city, state, country)
        weatherViewModel.getWeather(location, units)
    }

    when (weatherViewModel.weatherUiState) {
        is Init, Loading -> {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(modifier = Modifier.size(150.dp))
                Text(
                    text = stringResource(R.string.weather_loading_screen, city),
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
        is Success -> {
            ResultView(
                (weatherViewModel.weatherUiState as Success).weatherResult,
                (weatherViewModel.weatherUiState as Success).forecastResult,
                units, state
            )
        }
        is Error -> {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.error_getting_weather, city))
                Text(text = (weatherViewModel.weatherUiState as Error).errorMsg)
            }
        }
    }
}

@Composable
fun ResultView(
    weatherResult: WeatherResult, forecastResult: ForecastResult,
    units: String,
    state: String?
) {
    val degree = if (units == stringResource(R.string.imperial_unit))
        stringResource(R.string.deg_f) else stringResource(R.string.deg_c)
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        WeatherHeader(weatherResult, degree, state)
        ForecastDisplay(weatherResult, forecastResult, degree)
    }
}

@Composable
fun ForecastDisplay(weatherResult: WeatherResult, forecastResult: ForecastResult, degree: String) {
    val city = weatherResult.name
    val high = weatherResult.main?.tempMax
    val low = weatherResult.main?.tempMin
    val todaydt = weatherResult.dt!!.toLong()
    var calendar = Calendar.getInstance()
    calendar.timeInMillis = todaydt * 1000
    val dateToday = SimpleDateFormat(stringResource(id = R.string.day_pattern)).format(calendar.time)
    var startIndex = 0

    for (i in 0..9) {
        val dt = forecastResult.list!![i].dt!!.toLong()
        calendar = Calendar.getInstance()
        calendar.timeInMillis = dt * 1000
        val date = SimpleDateFormat(stringResource(id = R.string.day_pattern)).format(calendar.time)
        if (date != dateToday) {
            startIndex = i
            break
        }
    }

    val temperatureHighMap = mutableMapOf<String, Double>()
    val temperatureLowMap = mutableMapOf<String, Double>()
    for (i in startIndex..forecastResult.list!!.size - 1) {
        val temp = forecastResult.list!![i].main?.temp
        val dateTimeString = forecastResult.list!![i].dtTxt
        val date =
            SimpleDateFormat(stringResource(id = R.string.full_dt_format), Locale.getDefault()).parse(dateTimeString)
        val formattedDate = SimpleDateFormat(stringResource(id = R.string.ymd_format), Locale.getDefault()).format(date)

        if (!temperatureHighMap.containsKey(formattedDate) || temp!! > temperatureHighMap[formattedDate]!!) {
            temperatureHighMap[formattedDate] = temp!!
        }
        if (!temperatureLowMap.containsKey(formattedDate) || temp < temperatureLowMap[formattedDate]!!) {
            temperatureLowMap[formattedDate] = temp
        }
    }

    val highs = temperatureHighMap.map { it.value }.toMutableList()
    highs.add(0, high!!)
    val pointsData = mutableListOf<FloatEntry>()

    for (i in highs.indices) {
        pointsData += entryOf((i + 1).toFloat(), highs[i].toFloat())
    }

    val lows = temperatureLowMap.map { it.value }.toMutableList()
    lows.add(0, low!!)
    val pointsData2 = mutableListOf<FloatEntry>()
    for (i in lows.indices) {
        pointsData2 += entryOf((i + 1).toFloat(), lows[i].toFloat())
    }

    val formatter = SimpleDateFormat(stringResource(id = R.string.day_pattern))
    val horizontalAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
        SimpleDateFormat("E", Locale.getDefault()).format(formatter.parse(((value + dateToday.toInt() + 2)%365).toInt().toString()))
    }
    val verticalAxisValueFormatter = AxisValueFormatter<AxisPosition.Vertical.Start> { value, _ ->
        "${value.toInt()}$degree"
    }

    val yellow = Color(0xFFFFAA4A)
    val blue = Color(0xFF407EEB)

    Text(text = stringResource(id = R.string.next_5_days, city!!), fontSize = 20.sp, fontWeight = FontWeight.Medium)
    Chart(
        chart = lineChart(
            axisValuesOverrider = AxisValuesOverrider.fixed(
                minY = (min(lows) - 2).toInt().toFloat(),
                maxY = (max(highs) + 2).toInt().toFloat()
            ), lines = listOf(
                lineSpec(
                    lineColor = yellow,
                    lineBackgroundShader = verticalGradient(
                        arrayOf(yellow.copy(0.5f), yellow.copy(alpha = 0f)),
                    )
                )
            )
        ) + lineChart(
            axisValuesOverrider = AxisValuesOverrider.fixed(
                minY = (min(lows) - 2).toInt().toFloat(),
                maxY = (max(highs) + 2).toInt().toFloat(),
            ), lines = listOf(
                lineSpec(
                    lineColor = blue,
                    lineBackgroundShader = verticalGradient(
                        arrayOf(blue.copy(0.5f), blue.copy(alpha = 0f)),
                    )
                )
            )
        ),
        model = entryModelOf(pointsData) + entryModelOf(pointsData2),
        startAxis = rememberStartAxis(
            valueFormatter = verticalAxisValueFormatter,
            itemPlacer = AxisItemPlacer.Vertical.default(5),
        ),
        bottomAxis = rememberBottomAxis(
            valueFormatter = horizontalAxisValueFormatter,
        ),
        modifier = Modifier.padding(8.dp)
    )

    val key = temperatureHighMap.keys.first()
    DayCard(
        key,
        temperatureHighMap[key]!!.roundToInt(),
        temperatureLowMap[key]!!.roundToInt(),
        degree
    )
    for (day in temperatureHighMap.keys.toList().subList(1, temperatureHighMap.keys.size)) {
        Divider(
            color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .width(1.dp)
        )
        DayCard(
            day,
            temperatureHighMap[day]!!.roundToInt(),
            temperatureLowMap[day]!!.roundToInt(),
            degree
        )
    }
}

@Composable
fun WeatherHeader(weatherResult: WeatherResult, degree: String, state: String?) {
    val context = LocalContext.current
    val icon = weatherResult.weather?.get(0)?.icon
    val currentTemp = weatherResult.main?.temp
    val location = if (state.isNullOrEmpty() || state == " ") weatherResult.name else context.getString(
        R.string.city_state_text, weatherResult.name, state
    )
    val description = weatherResult.weather?.get(0)?.main
    val high = weatherResult.main?.tempMax
    val low = weatherResult.main?.tempMin

    Text(
        text = stringResource(id = R.string.city_card_format, location!!), fontSize = 32.sp, fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 32.dp)
    )
    Row(
        modifier = Modifier
            .padding(start = 12.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = stringResource(id = R.string.current_temp_string, currentTemp!!.toInt(), degree),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
            Text(text = stringResource(R.string.description_text, description!!), fontSize = 24.sp, fontWeight = FontWeight.Medium)
            Text(text = stringResource(
                R.string.high_low_text,
                high!!.toInt(),
                degree,
                low!!.toInt(),
                degree
            ), fontSize = 20.sp, fontWeight = FontWeight.Medium)
        }
        Spacer(modifier = Modifier.weight(1.0f))
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(
                    "https://openweathermap.org/img/w/$icon.png"
                ).crossfade(true)
                .build(),
            contentDescription = "Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
        )
    }
}

@Composable
fun DayCard(
    date: String,
    high: Int,
    low: Int,
    degree: String
) {

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val formatter = SimpleDateFormat(stringResource(id = R.string.ymd_format))
            val dayOfWeek = SimpleDateFormat(stringResource(R.string.full_day_name_format)).format(formatter.parse(date))
            Text(text = stringResource(R.string.day_of_week, dayOfWeek), fontSize = 20.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier =  Modifier.weight(1.0f))
            Column (
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.high_text, high, degree))
                Text(text = stringResource(R.string.low_text, low, degree))
            }
        }

}
