package com.xuie0000.hollowout.drawable

import android.app.Application
import timber.log.Timber

class HollowOutApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    Timber.plant(Timber.DebugTree())
  }
}