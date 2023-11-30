package hu.ait.weatherficks.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hu.ait.weatherficks.data.database.CitiesDAO
import hu.ait.weatherficks.data.database.CitiesDatabase
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    fun provideShopDao(appDatabase: CitiesDatabase): CitiesDAO {
        return appDatabase.citiesDao()
    }

    @Provides
    @Singleton
    fun provideShopAppDatabase(@ApplicationContext appContext: Context): CitiesDatabase {
        return CitiesDatabase.getDatabase(appContext)
    }
}