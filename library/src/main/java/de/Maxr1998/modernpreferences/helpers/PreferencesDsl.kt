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

@file:Suppress("unused")

package de.Maxr1998.modernpreferences.helpers

import android.content.Context
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.preferences.*

// PreferenceScreen DSL functions
inline fun screen(context: Context?, block: PreferenceScreen.Builder.() -> Unit): PreferenceScreen {
    return PreferenceScreen.Builder(context).apply(block).build()
}

val emptyScreen: PreferenceScreen by lazy { screen(null) {} }

inline fun PreferenceScreen.Builder.subScreen(key: String = "", block: PreferenceScreen.Builder.() -> Unit) {
    addPreferenceItem(PreferenceScreen.Builder(this, key).apply(block).build())
}

// Preference DSL functions
inline fun PreferenceScreen.Builder.categoryHeader(key: String, block: Preference.() -> Unit) {
    addPreferenceItem(CategoryHeader(key).apply(block))
}

inline fun PreferenceScreen.Builder.pref(key: String, block: Preference.() -> Unit): Preference {
    val p = Preference(key).apply(block)
    addPreferenceItem(p)
    return p
}

inline fun PreferenceScreen.Builder.accentButtonPref(key: String, block: Preference.() -> Unit): Preference {
    val abp = AccentButtonPreference(key).apply(block)
    addPreferenceItem(abp)
    return abp
}

inline fun PreferenceScreen.Builder.switch(key: String, block: SwitchPreference.() -> Unit): SwitchPreference {
    val sp = SwitchPreference(key).apply(block)
    addPreferenceItem(sp)
    return sp
}

inline fun PreferenceScreen.Builder.checkBox(key: String, block: CheckBoxPreference.() -> Unit): CheckBoxPreference {
    val cbp = CheckBoxPreference(key).apply(block)
    addPreferenceItem(cbp)
    return cbp
}

inline fun PreferenceScreen.Builder.image(key: String, block: ImagePreference.() -> Unit): ImagePreference {
    val img = ImagePreference(key).apply(block)
    addPreferenceItem(img)
    return img
}

inline fun PreferenceScreen.Builder.seekBar(key: String, block: SeekBarPreference.() -> Unit): SeekBarPreference {
    val sbp = SeekBarPreference(key).apply(block)
    addPreferenceItem(sbp)
    return sbp
}

inline fun PreferenceScreen.Builder.expandText(key: String, block: ExpandableTextPreference.() -> Unit): ExpandableTextPreference {
    val etp = ExpandableTextPreference(key).apply(block)
    addPreferenceItem(etp)
    return etp
}

inline fun <reified T : Preference> PreferenceScreen.Builder.custom(key: String, block: T.() -> Unit): T {
    val c = T::class.java.getConstructor(String::class.java).newInstance(key).apply(block)
    addPreferenceItem(c)
    return c
}

inline fun PreferenceScreen.Builder.collapse(key: String = "advanced", block: PreferenceScreen.Builder.() -> Unit) {
    collapseNext(key)
    block()
    collapseEnd()
}

// Listener helpers
inline fun Preference.onClicked(crossinline callback: (Preference) -> Boolean) {
    clickListener = object : Preference.OnClickListener {
        override fun onClick(preference: Preference, holder: PreferencesAdapter.ViewHolder) =
                callback(preference)
    }
}

inline fun Preference.onClickView(crossinline callback: (Preference, PreferencesAdapter.ViewHolder) -> Boolean) {
    clickListener = object : Preference.OnClickListener {
        override fun onClick(preference: Preference, holder: PreferencesAdapter.ViewHolder) =
                callback(preference, holder)
    }
}

inline fun TwoStatePreference.onChange(crossinline callback: (TwoStatePreference, PreferencesAdapter.ViewHolder?, Boolean) -> Boolean) {
    checkedChangeListener = object : TwoStatePreference.OnCheckedChangeListener {
        override fun onCheckedChanged(preference: TwoStatePreference, holder: PreferencesAdapter.ViewHolder?, checked: Boolean) =
                callback(preference, holder, checked)
    }
}

inline fun SeekBarPreference.onSeek(crossinline callback: (SeekBarPreference, PreferencesAdapter.ViewHolder?, Int) -> Boolean) {
    seekListener = object : SeekBarPreference.OnSeekListener {
        override fun onSeek(preference: SeekBarPreference, holder: PreferencesAdapter.ViewHolder?, value: Int) =
                callback(preference, holder, value)
    }
}