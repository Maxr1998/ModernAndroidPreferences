/*
 * Copyright (C) 2018 Max Rumpf alias Maxr1998
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.Maxr1998.modernpreferences.preferences

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Space
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.R
import de.Maxr1998.modernpreferences.helpers.onSeekUser

class SeekBarPreference(key: String) : Preference(key) {

    var min = 0
    var max = 0

    override fun getWidgetLayoutResource() = R.layout.preference_widget_empty

    override fun bindViews(holder: PreferencesAdapter.ViewHolder) {
        super.bindViews(holder)
        (holder.itemView as ViewGroup).apply {
            background = null
            clipChildren = false
        }
        val widget = holder.widget as Space
        val inflater = LayoutInflater.from(widget.context)
        val seekBar = (widget.tag ?: inflater.inflate(R.layout.preference_widget_seekbar,
                holder.itemView).findViewById(android.R.id.progress)) as SeekBar
        widget.tag = seekBar
        seekBar.apply {
            //min = this@SeekBarPreference.min
            max = this@SeekBarPreference.max
            progress = getInt(0)
            onSeekUser(::commitInt)
        }
    }
}