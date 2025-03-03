package com.example.frontend

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp  // ✅ REQUIRED for Hilt dependency injection
class MainApplication : Application()
