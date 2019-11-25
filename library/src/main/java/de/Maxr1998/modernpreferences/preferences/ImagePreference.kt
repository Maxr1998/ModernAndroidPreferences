package de.Maxr1998.modernpreferences.preferences

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.R

class ImagePreference(key: String) : Preference(key) {
    override fun getWidgetLayoutResource() = RESOURCE_CONST

    @DrawableRes
    var imageRes: Int = -1
    var lazyImage: (() -> Drawable)? = null
    var imageDrawable: Drawable? = null

    var showScrim = true

    override fun bindViews(holder: PreferencesAdapter.ViewHolder) {
        super.bindViews(holder)
        val image = holder.root.findViewById<ImageView>(R.id.map_image)
        when {
            imageRes != -1 -> image.setImageResource(imageRes)
            lazyImage != null -> image.setImageDrawable(lazyImage?.invoke())
            imageDrawable != null -> image.setImageDrawable(imageDrawable)
            else -> image.setImageDrawable(null)
        }
        val scrim = holder.root.findViewById<ImageView>(R.id.map_image_scrim)
        scrim.isVisible = showScrim && title.isNotBlank()
    }

    companion object {
        const val RESOURCE_CONST = -4
    }
}