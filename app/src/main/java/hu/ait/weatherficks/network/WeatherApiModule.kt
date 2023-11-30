package hu.ait.weatherficks.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object WeatherApiModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org")
            .addConverterFactory(Json{ ignoreUnknownKeys = true }.asConverterFactory("application/json".toMediaType()) )
            //.addConverterFactory(ScalarsConverterFactory.create()) // For raw string results
            .build()
    }

    @Provides
    @Singleton
    fun provideCurrentWeatherAPI(retrofit: Retrofit): CurrentWeatherAPI {
        return retrofit.create(CurrentWeatherAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideWeatherForecastAPI(retrofit: Retrofit): WeatherForecastAPI {
        return retrofit.create(WeatherForecastAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideLocationAPI(retrofit: Retrofit): LocationAPI {
        return retrofit.create(LocationAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideVerifierAPI(retrofit: Retrofit): VerifierAPI {
        return retrofit.create(VerifierAPI::class.java)
    }
}