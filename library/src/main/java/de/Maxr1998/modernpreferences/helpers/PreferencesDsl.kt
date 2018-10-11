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

package de.Maxr1998.modernpreferences.helpers

import android.content.Context
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.preferences.CategoryHeader
import de.Maxr1998.modernpreferences.preferences.CheckBoxPreference
import de.Maxr1998.modernpreferences.preferences.SwitchPreference
import de.Maxr1998.modernpreferences.preferences.TwoStatePreference

inline fun screen(context: Context?, block: PreferenceScreen.Builder.() -> Unit): PreferenceScreen {
    return PreferenceScreen.Builder(context).apply(block).build()
}

val emptyScreen: PreferenceScreen by lazy { screen(null) {} }

inline fun PreferenceScreen.Builder.subScreen(block: PreferenceScreen.Builder.() -> Unit) {
    addPreferenceItem(PreferenceScreen.Builder(this).apply(block).build())
}

inline fun PreferenceScreen.Builder.categoryHeader(key: String, block: Preference.() -> Unit) {
    addPreferenceItem(CategoryHeader(key).apply(block))
}

inline fun PreferenceScreen.Builder.pref(key: String, block: Preference.() -> Unit): Preference {
    val p = Preference(key).apply(block)
    addPreferenceItem(p)
    return p
}

inline fun PreferenceScreen.Builder.switch(key: String, block: SwitchPreference.() -> Unit): SwitchPreference {
    val sp = SwitchPreference(key).apply(block)
    addPreferenceItem(sp)
    return sp
}

inline fun PreferenceScreen.Builder.checkBox(key: String, block: CheckBoxPreference.() -> Unit): CheckBoxPreference {
    val cp = CheckBoxPreference(key).apply(block)
    addPreferenceItem(cp)
    return cp
}

inline fun PreferenceScreen.Builder.collapse(key: String = "advanced", block: PreferenceScreen.Builder.() -> Unit) {
    collapseNext(key)
    block()
    collapseEnd()
}

inline fun Preference.click(crossinline callback: (Preference) -> Boolean) {
    clickListener = object : Preference.OnClickListener {
        override fun onClick(preference: Preference, holder: PreferencesAdapter.ViewHolder) =
                callback(preference)
    }
}

inline fun Preference.clickView(crossinline callback: (Preference, PreferencesAdapter.ViewHolder) -> Boolean) {
    clickListener = object : Preference.OnClickListener {
        override fun onClick(preference: Preference, holder: PreferencesAdapter.ViewHolder) =
                callback(preference, holder)
    }
}

inline fun TwoStatePreference.changed(crossinline callback: (TwoStatePreference, PreferencesAdapter.ViewHolder?, Boolean) -> Boolean) {
    checkedChangeListener = object : TwoStatePreference.OnCheckedChangeListener {
        override fun onCheckedChanged(preference: TwoStatePreference, holder: PreferencesAdapter.ViewHolder?, checked: Boolean) =
                callback(preference, holder, checked)
    }
}

inline fun <reified T : Preference> PreferenceScreen.Builder.custom(key: String, block: T.() -> Unit): T {
    val c = T::class.java.getConstructor(String::class.java).newInstance(key).apply(block)
    addPreferenceItem(c)
    return c
}