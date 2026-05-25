package com.tecnm.staffconnect.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.tecnm.staffconnect.data.local.StaffConnectDatabase
import com.tecnm.staffconnect.data.local.VacacionDao
import com.tecnm.staffconnect.data.remote.AuthApi
import com.tecnm.staffconnect.data.remote.AuthInterceptor
import com.tecnm.staffconnect.data.remote.StaffConnectApi
import com.tecnm.staffconnect.data.repository.AuthRepositoryImpl
import com.tecnm.staffconnect.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton
import com.tecnm.staffconnect.data.local.NominaDao

private val Context.dataStore: DataStore<Preferences>
        by preferencesDataStore(name = "staffconnect_prefs")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val MOCKAPI_URL = "https://6a0fa87ed2a985707035adf6.mockapi.io/"
    private const val AUTH_URL = "https://6a109e93d2a985707036fead.mockapi.io/"

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        dataStore: DataStore<Preferences>
    ): AuthInterceptor {
        return AuthInterceptor(dataStore)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("main")
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val gson = com.google.gson.GsonBuilder()
            .setLenient()
            .create()
        return Retrofit.Builder()
            .baseUrl(MOCKAPI_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    @Named("auth")
    fun provideAuthRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AUTH_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideStaffConnectApi(
        @Named("main") retrofit: Retrofit
    ): StaffConnectApi {
        return retrofit.create(StaffConnectApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthApi(
        @Named("auth") retrofit: Retrofit
    ): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): StaffConnectDatabase {
        return Room.databaseBuilder(
            context,
            StaffConnectDatabase::class.java,
            "staffconnect_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideVacacionDao(
        database: StaffConnectDatabase
    ): VacacionDao {
        return database.vacacionDao()
    }

    @Provides
    @Singleton
    fun provideNominaDao(database: StaffConnectDatabase): NominaDao {
        return database.nominaDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository
}