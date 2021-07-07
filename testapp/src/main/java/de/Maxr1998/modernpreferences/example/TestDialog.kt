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

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AlertDialog
import de.Maxr1998.modernpreferences.preferences.DialogPreference

class TestDialog : DialogPreference("dialog") {
    override fun createDialog(context: Context): Dialog = createDialog(context, 0)
    override fun createDialog(context: Context, style: Int): Dialog =
            AlertDialog.Builder(context)
                    .setTitle("Info")
                    .setMessage("You opened this dialog!")
                    .setPositiveButton(android.R.string.ok, null)
                    .create()
}