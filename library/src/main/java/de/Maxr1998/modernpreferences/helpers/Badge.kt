package de.Maxr1998.modernpreferences.helpers

import android.content.res.ColorStateList
import androidx.annotation.StringRes

data class Badge(
    @StringRes var textRes: Int = DEFAULT_RES_ID,
    var text: CharSequence? = null,
    var badgeColor: ColorStateList? = null,
)