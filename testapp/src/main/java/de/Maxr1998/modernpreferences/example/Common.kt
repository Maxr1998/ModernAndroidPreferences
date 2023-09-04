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
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.helpers.accentButtonPref
import de.Maxr1998.modernpreferences.helpers.categoryHeader
import de.Maxr1998.modernpreferences.helpers.checkBox
import de.Maxr1998.modernpreferences.helpers.collapse
import de.Maxr1998.modernpreferences.helpers.editText
import de.Maxr1998.modernpreferences.helpers.expandText
import de.Maxr1998.modernpreferences.helpers.image
import de.Maxr1998.modernpreferences.helpers.multiChoice
import de.Maxr1998.modernpreferences.helpers.pref
import de.Maxr1998.modernpreferences.helpers.screen
import de.Maxr1998.modernpreferences.helpers.seekBar
import de.Maxr1998.modernpreferences.helpers.singleChoice
import de.Maxr1998.modernpreferences.helpers.subScreen
import de.Maxr1998.modernpreferences.helpers.switch
import de.Maxr1998.modernpreferences.preferences.Badge
import de.Maxr1998.modernpreferences.preferences.SeekBarPreference
import de.Maxr1998.modernpreferences.preferences.choice.SelectionItem
import java.util.Locale

object Common {
    init {
        Preference.Config.dialogBuilderFactory = { context ->
            MaterialAlertDialogBuilder(context)
        }
    }

    @Suppress("LongMethod", "MagicNumber")
    fun createRootScreen(context: Context) = screen(context) {
        subScreen("types") {
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
            pref("with-badge") {
                title = "And badges!"
                badgeInfo = Badge("pro", ColorStateList.valueOf(Color.RED))
            }
            accentButtonPref("accent-button") {
                title = "Button style".uppercase(Locale.getDefault())
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
                clickListener = Preference.OnClickListener { _, holder ->
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
            image("image-kotlin") {
                imageRes = R.drawable.ic_kotlin
                showScrim = false
            }
            image("image-earth") {
                title = "\u00A9 2019 DigitalGlobe"
                val imageStream = context.assets.open("earthview_6300.jpg")
                imageDrawable = BitmapDrawable.createFromStream(imageStream, null)
            }
            seekBar("seekbar") {
                title = "A seekbar"
                min = 1
                max = 100
            }
            seekBar("seekbar-stepped") {
                title = "A seekbar with steps"
                min = -100
                step = 10
                max = 100
            }
            seekBar("seekbar-ticks") {
                title = "A seekbar with tick marks"
                min = 1
                max = 10
                showTickMarks = true
            }
            seekBar("seekbar-default") {
                title = "A seekbar with a default value"
                min = 1
                max = 5
                default = 3

                // Callback listener
                seekListener = SeekBarPreference.OnSeekListener { _, _, i ->
                    Log.d("Preferences", "SeekBar changed to $i")
                    true
                }
            }
            +TestDialog().apply {
                title = "Show dialog"
                iconRes = R.drawable.ic_info_24dp
            }
            val selectableItems = listOf(
                SelectionItem("key_0", "Option 1", null),
                SelectionItem("key_1", "Option 2", "Second option"),
                SelectionItem("key_2", "Option 3", "You can put anything you want into this summary!"),
                SelectionItem("key_3", "Option 4", "Even supports badges!", Badge("pro")),
            )
            singleChoice("single-choice-dialog", selectableItems) {
                title = "Single choice selection dialog"
                summary = "Only one item is selectable, de-selection is impossible"
            }
            multiChoice("multi-choice-dialog", selectableItems) {
                title = "Multi choice selection dialog"
                summary = "None, one or multiple items are selectable"
            }
            editText("edit-text") {
                title = "Text input"
                textInputHint = "Enter whatever you want!"
            }
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
        subScreen("list") {
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