package de.Maxr1998.modernpreferences.preferences

import android.content.res.ColorStateList
import androidx.annotation.StringRes
import de.Maxr1998.modernpreferences.helpers.DEFAULT_RES_ID

data class Badge internal constructor(
    @StringRes
    val textRes: Int = DEFAULT_RES_ID,
    val text: CharSequence? = null,
    val badgeColor: ColorStateList? = null,
) {
    constructor(text: CharSequence?, badgeColor: ColorStateList? = null) : this(
        textRes = DEFAULT_RES_ID,
        text = text,
        badgeColor = badgeColor,
    )

    constructor(@StringRes textRes: Int, badgeColor: ColorStateList? = null) : this(
        textRes = textRes,
        text = null,
        badgeColor = badgeColor,
    )

    val isVisible: Boolean
        get() = textRes != DEFAULT_RES_ID || text != null
}