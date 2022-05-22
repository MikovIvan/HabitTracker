package ru.mikov.habittracker.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.mikov.data.remote.NetworkMonitor
import ru.mikov.data.remote.RestService
import ru.mikov.data.remote.RestService.Companion.BASE_URL
import ru.mikov.data.remote.interceptors.ErrorStatusInterceptor
import ru.mikov.data.remote.interceptors.NetworkStatusInterceptor
import ru.mikov.data.remote.interceptors.TokenInterceptor
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideRestService(retrofit: Retrofit): RestService =
        retrofit.create(RestService::class.java)

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit = Retrofit.Builder()
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(
        logging: HttpLoggingInterceptor,
        token: TokenInterceptor,
        statusInterceptor: NetworkStatusInterceptor,
        errorStatusInterceptor: ErrorStatusInterceptor
    ): OkHttpClient = OkHttpClient().newBuilder()
        .addInterceptor(token)
        .addInterceptor(statusInterceptor)
        .addInterceptor(logging)
        .addInterceptor(errorStatusInterceptor)
        .build()

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    fun provideNetworkStatusInterceptor(monitor: NetworkMonitor): NetworkStatusInterceptor =
        NetworkStatusInterceptor(monitor)

    @Provides
    @Singleton
    fun provideErrorStatusInterceptor(moshi: Moshi): ErrorStatusInterceptor =
        ErrorStatusInterceptor(moshi)

    @Provides
    @Singleton
    fun provideTokenAuthenticator(): TokenInterceptor = TokenInterceptor()

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideNetworkMonitor(context: Context): NetworkMonitor = NetworkMonitor(context)
}