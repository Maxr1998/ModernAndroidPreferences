package de.Maxr1998.modernpreferences.testing

import de.Maxr1998.modernpreferences.PreferencesAdapter
import io.mockk.every
import io.mockk.spyk

val uniqueKeySequence = iterator {
    var i = 0
    while (true) {
        yield("key_${i++}")
    }
}

fun createPreferenceAdapter(): PreferencesAdapter = spyk(PreferencesAdapter(hasStableIds = false)) {
    every { notifyDataSetChanged() } returns Unit
}