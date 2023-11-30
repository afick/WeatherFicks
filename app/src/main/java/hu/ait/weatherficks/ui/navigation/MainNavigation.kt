package hu.ait.weatherficks.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Cities : Screen("cities")
    object Weather : Screen("weather/{city}/{state}/{country}/{units}")
}