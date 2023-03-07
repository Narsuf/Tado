package org.n27.tado.data.api.injection

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import org.n27.tado.data.api.TadoApi
import org.n27.tado.data.api.TadoAuth
import retrofit2.Retrofit.Builder
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
class NetModule {

    @Provides
    @Singleton
    fun providesOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

    @Provides
    @Singleton
    fun providesMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Provides
    @Singleton
    fun providesAuth(okHttpClient: OkHttpClient, moshi: Moshi): TadoAuth {
        return Builder().client(okHttpClient).baseUrl("https://auth.tado.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build().create(TadoAuth::class.java)
    }

    @Provides
    @Singleton
    fun providesApi(okHttpClient: OkHttpClient, moshi: Moshi): TadoApi {
        return Builder().client(okHttpClient).baseUrl("https://my.tado.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build().create(TadoApi::class.java)
    }
}
