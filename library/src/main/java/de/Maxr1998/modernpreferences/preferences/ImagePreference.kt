package de.Maxr1998.modernpreferences.preferences

import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.R

/**
 * Shows a drawable inside an ImageView
 *
 * The drawable can be set via [imageRes], [imageDrawable], or lazily via [lazyImage];
 * with priorities being evaluated in the same order.
 *
 * By default, a scrim is shown over the image to improve [title] legibility, which can be disabled
 * by setting [showScrim] to false.
 *
 * The image can be tinted by applying a [ColorFilter], either via [tint],
 * or through the helper method [setTintColor].
 */
@Suppress("MemberVisibilityCanBePrivate")
class ImagePreference(key: String) : Preference(key) {
    override fun getWidgetLayoutResource() = RESOURCE_CONST

    @DrawableRes
    var imageRes: Int = -1
    var imageDrawable: Drawable? = null
    var lazyImage: (() -> Drawable)? = null

    var showScrim = true
    var tint: ColorFilter? = null

    fun setTintColor(@ColorInt color: Int, mode: PorterDuff.Mode = PorterDuff.Mode.SRC_ATOP) {
        tint = PorterDuffColorFilter(color, mode)
    }

    override fun bindViews(holder: PreferencesAdapter.ViewHolder) {
        super.bindViews(holder)
        val image = holder.root.findViewById<ImageView>(R.id.map_image)
        when {
            imageRes != -1 -> image.setImageResource(imageRes)
            imageDrawable != null -> image.setImageDrawable(imageDrawable)
            lazyImage != null -> image.setImageDrawable(lazyImage?.invoke())
            else -> image.setImageDrawable(null)
        }
        val scrim = holder.root.findViewById<ImageView>(R.id.map_image_scrim)
        scrim.isVisible = showScrim && title.isNotBlank()
        image.colorFilter = tint
    }

    companion object {
        const val RESOURCE_CONST = -4
    }
}