package com.example.globalapp.retrofit

import com.example.globalapp.util.Constants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoginModule {

    val gson: Gson = GsonBuilder()
        .serializeNulls()
        .create()

    @Singleton
    @Provides
    fun providesRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
//            .baseUrl("http://192.168.1.93:3001/api-vi/")//TODO ver porque no me acepta agegar la ruta asi
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Singleton
    @Provides
    fun providesLogin(retrofit: Retrofit):LoginClient{
        return retrofit.create(LoginClient::class.java)
    }

//    @Singleton
//    @Provides
//    fun providesPropuestas(retrofit: Retrofit):LoginClient{
//        return retrofit.create(LoginClient::class.java)
//    }
}