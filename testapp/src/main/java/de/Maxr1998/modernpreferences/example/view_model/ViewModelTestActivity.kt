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
import androidx.activity.viewModels
import de.Maxr1998.modernpreferences.example.BaseActivity

class ViewModelTestActivity : BaseActivity() {

    private val viewModel: MainViewModel by viewModels()
    override val preferencesAdapter get() = viewModel.preferencesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferencesAdapter.restoreAndObserveScrollPosition(preferencesView)
        onScreenChanged(preferencesAdapter.currentScreen, preferencesAdapter.isInSubScreen())
        preferencesAdapter.onScreenChangeListener = this
    }
}