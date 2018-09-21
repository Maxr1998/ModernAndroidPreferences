package de.Maxr1998.modernpreferences.example.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.example.Common

class MainViewModel(app: Application) : AndroidViewModel(app) {

    val preferencesAdapter = PreferencesAdapter().apply {
        setRootScreen(Common.createRootScreen(getApplication()))
    }
}