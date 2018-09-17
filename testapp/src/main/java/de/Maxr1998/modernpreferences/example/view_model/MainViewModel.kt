package de.Maxr1998.modernpreferences.example.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.*

class MainViewModel(app: Application) : AndroidViewModel(app) {

    val preferencesAdapter = PreferencesAdapter()

    init {
        preferencesAdapter.setRootScreen(screen(getApplication()) {
            pref("one") {
                title = "Important"
                description = "It actually is!"
            }
            pref("two") {
                title = "Also important"
            }
            subScreen {
                title = "Subscreen"

                pref("three") {
                    title = "A not so important sub-preference"
                }
            }
            categoryHeader("header") {
                title = "More"
            }
            switch("four") {
                title = "Switch"
                description = "This is a switch"
            }
        })
    }
}