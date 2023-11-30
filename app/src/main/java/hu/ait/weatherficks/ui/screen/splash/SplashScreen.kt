package hu.ait.weatherficks.ui.screen.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.ait.weatherficks.R
import kotlinx.coroutines.delay

@Composable
fun Splash(onNavigateToCities: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val scale = remember {
            Animatable(0.0f)
        }
        LaunchedEffect(key1 = Unit) {
            scale.animateTo(
                targetValue = 0.8f,
                animationSpec = tween(1500)
            )
            // 3 second delay then navigate to main screen
            delay(1500)
            onNavigateToCities()
        }
        Image(
            painter = painterResource(id = R.drawable.weather),
            contentDescription = "icon",
            alignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .scale(scale.value)
        )
        Text(
            text = stringResource(R.string.App_name),
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 180.dp)
        )
    }
}