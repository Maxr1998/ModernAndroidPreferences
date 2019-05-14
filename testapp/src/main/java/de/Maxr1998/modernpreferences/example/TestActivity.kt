/*
 * ModernAndroidPreferences Example Application
 * Copyright (C) 2018  Max Rumpf alias Maxr1998
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.Maxr1998.modernpreferences.example

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter

class TestActivity : AppCompatActivity(), PreferencesAdapter.OnScreenChangeListener {

    private lateinit var preferenceView: RecyclerView
    private val preferencesAdapter = PreferencesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferenceView = RecyclerView(this)
        setContentView(preferenceView)
        preferenceView.layoutManager = LinearLayoutManager(this)
        preferenceView.adapter = preferencesAdapter

        preferencesAdapter.setRootScreen(Common.createRootScreen(this))
        preferencesAdapter.onScreenChangeListener = this
        // Restore adapter state from saved state
        savedInstanceState?.getParcelable<PreferencesAdapter.SavedState>("adapter")
                ?.let(preferencesAdapter::loadSavedState)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        // Save the adapter state as a parcelable into the Android-managed instance state
        outState?.putParcelable("adapter", preferencesAdapter.getSavedState())
    }

    override fun onScreenChanged(screen: PreferenceScreen, subScreen: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(subScreen)
        screen["25"]?.let { pref ->
            val viewOffset = ((preferenceView.height - 64 * resources.displayMetrics.density) / 2).toInt()
            (preferenceView.layoutManager as? LinearLayoutManager)
                    ?.scrollToPositionWithOffset(pref.screenPosition, viewOffset)
            pref.requestRebindAndHighlight()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (!preferencesAdapter.goBack())
            super.onBackPressed()
    }

    override fun onDestroy() {
        preferencesAdapter.onScreenChangeListener = null
        super.onDestroy()
    }
}