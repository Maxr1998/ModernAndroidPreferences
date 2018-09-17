package de.Maxr1998.modernpreferences.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.*

class TestActivity : AppCompatActivity() {

    private val preferencesAdapter = PreferencesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferenceView = RecyclerView(this)
        setContentView(preferenceView)
        preferenceView.layoutManager = LinearLayoutManager(this)
        preferenceView.adapter = preferencesAdapter

        preferencesAdapter.setRootScreen(screen(this) {
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

    override fun onBackPressed() {
        if (!preferencesAdapter.goBack())
            super.onBackPressed()
    }
}