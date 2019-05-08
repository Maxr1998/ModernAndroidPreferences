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

import android.content.Context
import android.widget.Toast
import de.Maxr1998.modernpreferences.helpers.*

object Common {
    fun createRootScreen(context: Context) = screen(context) {
        subScreen {
            title = "Preference types"
            summary = "Overview over all the different preference items, with various widgets"
            iconRes = R.drawable.ic_apps_24dp
            centerIcon = false

            categoryHeader("header_plain") {
                title = "Plain"
            }
            pref("plain") {
                title = "A plain preference…"
            }
            pref("with-summary") {
                title = "…that doesn't have a widget"
                summary = "But a summary this time!"
            }
            pref("with-icon") {
                title = "There's also icon support, yay!"
                iconRes = R.drawable.ic_emoji_24dp
            }
            accentButtonPref("accent-button") {
                title = "Button style!"
            }
            categoryHeader("header_two_state") {
                title = "Two state"
            }
            switch("switch") {
                title = "A simple switch"
            }
            pref("dependent") {
                title = "Toggle the switch above"
                dependency = "switch"
                onClickView { _, holder ->
                    Toast.makeText(holder.itemView.context, "Preference was clicked!", Toast.LENGTH_SHORT).show()
                    false
                }
            }
            checkBox("checkbox") {
                title = "A checkbox"
            }
            categoryHeader("header_advanced") {
                title = "Advanced"
            }
            seekBar("seekbar") {
                title = "A seekbar"
                min = 1
                max = 100
            }
            seekBar("seekbar-stepped") {
                title = "A seekbar with steps"
                min = 100
                max = 1000
                step = 25
            }
            addPreferenceItem(TestDialog().apply {
                title = "Show dialog"
                iconRes = R.drawable.ic_info_24dp
            })
            expandText("expand-text") {
                title = "Expandable text"
                text = "This is an example implementation of ModernAndroidPreferences, check out " +
                        "the source on https://github.com/Maxr1998/ModernAndroidPreferences"
            }
            collapse {
                pref("collapsed_one") {
                    title = "Collapsed by default"
                }
                pref("collapsed_two") {
                    title = "Another preference"
                }
                pref("collapsed_three") {
                    title = "A longer title to trigger ellipsize"
                }
            }
        }
        subScreen {
            title = "Long list"
            summary = "A longer list to see how well library performs thanks to the backing RecyclerView"
            iconRes = R.drawable.ic_list_24dp
            collapseIcon = true

            for (i in 1..100) {
                pref(i.toString()) {
                    title = "Preference item #$i"
                    summary = "Lorem ipsum …"
                }
            }
        }
    }
}