package hu.ait.weatherficks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import hu.ait.weatherficks.ui.screen.cities.CitiesScreen
import hu.ait.weatherficks.ui.screen.splash.Splash
import hu.ait.weatherficks.ui.screen.weather.WeatherScreen
import hu.ait.weatherficks.ui.navigation.Screen
import hu.ait.weatherficks.ui.theme.WeatherFicksTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherFicksTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherNavHost()
                }
            }
        }
    }
}

@Composable
fun WeatherNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController, startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            Splash(
                onNavigateToCities =  {
                    navController.navigate(Screen.Cities.route)
                }
            )
        }
        composable(Screen.Cities.route) {
            CitiesScreen(
                onNavigateToWeather = {
                    city, state, country, units -> navController.navigate("weather/$city/$state/$country/$units")
                }
            )
        }
        composable(Screen.Weather.route,
            arguments = listOf(
                navArgument("city"){type = NavType.StringType},
                navArgument("state"){type = NavType.StringType},
                navArgument("country"){type = NavType.StringType},
                navArgument("units"){type = NavType.StringType}
            )
        ) {
            val city = it.arguments?.getString("city")
            val state = it.arguments?.getString("state")
            val country = it.arguments?.getString("country")
            val units = it.arguments?.getString("units")
            WeatherScreen(
                city = city!!,
                state = state,
                country = country!!,
                units = units!!
            )
        }
    }
}