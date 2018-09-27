package de.Maxr1998.modernpreferences.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.R

class CollapsePreference(key: String) : Preference(key) {
    private val preferences = ArrayList<Preference>()

    init {
        titleRes = R.string.pref_advanced_block
        iconRes = R.drawable.ic_expand_24dp
    }

    fun addItem(preference: Preference) {
        preferences.add(preference.apply { visible = false })
    }

    /*
     This makes sure we don't get recycled for any other preference.
     Hopefully, no one else uses this constant for the exact same purpose…
     But that will probably not be our problem.
     */
    @SuppressLint("ResourceType")
    override fun getWidgetLayoutResource() = -10

    override fun bindViews(holder: PreferencesAdapter.ViewHolder) {
        if (visible) buildSummary(holder.itemView.context)
        super.bindViews(holder)
        holder.summary?.apply {
            setSingleLine()
            ellipsize = TextUtils.TruncateAt.END
        }
    }

    private fun buildSummary(context: Context) {
        if (summary != null) return

        val titles = ArrayList<String>()
        for (i in 0..Math.min(5, preferences.size - 1)) {
            titles += preferences[i].run {
                when {
                    titleRes != -1 -> context.getString(titleRes)
                    else -> title
                }
            }
        }
        summary = titles.joinToString(", ")
    }

    override fun onClick(holder: PreferencesAdapter.ViewHolder) {
        visible = false
        for (i in preferences.indices)
            preferences[i].visible = true
        attachedScreen?.requestRebind(screenPosition, 1 + preferences.size)
    }
}