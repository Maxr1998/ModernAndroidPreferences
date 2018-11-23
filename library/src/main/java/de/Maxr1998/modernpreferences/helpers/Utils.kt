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

import android.widget.SeekBar

internal fun SeekBar.onSeekUser(callback: (Int) -> Unit) {
    setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        private var old = 0
        private var new = 0

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            if (fromUser) new = progress
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
            old = seekBar.progress
            new = old
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            if (old != new) callback(new)
        }
    })
}