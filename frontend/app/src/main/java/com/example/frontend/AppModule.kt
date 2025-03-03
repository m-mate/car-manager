package com.example.frontend


import android.content.Context
import android.content.SharedPreferences
import com.example.frontend.CarApiService
import com.example.frontend.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // ✅ Replace ApplicationComponent with SingletonComponent
object AppModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideApiService(@ApplicationContext context: Context): CarApiService {
        return RetrofitClient.create(context, "").create(CarApiService::class.java)
    }
}
