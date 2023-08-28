package de.Maxr1998.modernpreferences.helpers

import android.content.res.ColorStateList
import androidx.annotation.StringRes

class Badge private constructor(
    @StringRes var textRes: Int = DEFAULT_RES_ID,
    var text: CharSequence? = null,
    var badgeColor: ColorStateList? = null,
){
    constructor(text: CharSequence?, badgeColor: ColorStateList? = null) : this(
        textRes = DEFAULT_RES_ID,
        text = text,
        badgeColor = badgeColor
    )
    constructor(@StringRes textRes: Int, badgeColor: ColorStateList? = null) : this(
        textRes = textRes,
        text = null,
        badgeColor = badgeColor
    )
}