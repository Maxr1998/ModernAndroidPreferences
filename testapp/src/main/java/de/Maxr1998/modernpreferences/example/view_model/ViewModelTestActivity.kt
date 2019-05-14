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

package de.Maxr1998.modernpreferences.example.view_model

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter

class ViewModelTestActivity : AppCompatActivity(), PreferencesAdapter.OnScreenChangeListener {

    private lateinit var viewModel: MainViewModel
    private lateinit var preferenceView: RecyclerView
    private val preferencesAdapter get() = viewModel.preferencesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get<MainViewModel>()

        preferenceView = RecyclerView(this)
        setContentView(preferenceView)
        val layoutManager = LinearLayoutManager(this)
        preferenceView.layoutManager = layoutManager
        preferenceView.adapter = preferencesAdapter

        preferencesAdapter.restoreAndObserveScrollPosition(preferenceView)
        onScreenChanged(preferencesAdapter.currentScreen, preferencesAdapter.isInSubScreen())
        preferencesAdapter.onScreenChangeListener = this
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
        if (!viewModel.preferencesAdapter.goBack())
            super.onBackPressed()
    }

    override fun onDestroy() {
        preferencesAdapter.onScreenChangeListener = null
        super.onDestroy()
    }
}