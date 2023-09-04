package de.Maxr1998.modernpreferences.preferences.choice

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import de.Maxr1998.modernpreferences.R
import de.Maxr1998.modernpreferences.helpers.DEFAULT_RES_ID

internal class SelectionAdapter(
    private val preference: AbstractChoiceDialogPreference,
    private val items: List<SelectionItem>,
    private val allowMultiSelect: Boolean,
) : RecyclerView.Adapter<SelectionAdapter.SelectionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val layout = when {
            allowMultiSelect -> R.layout.map_dialog_multi_choice_item
            else -> R.layout.map_dialog_single_choice_item
        }
        val view = layoutInflater.inflate(layout, parent, false)
        return SelectionViewHolder(view)
    }

    @Suppress("ComplexMethod", "NestedBlockDepth")
    override fun onBindViewHolder(holder: SelectionViewHolder, position: Int) {
        val item = items[position]
        holder.apply {
            val attrs = intArrayOf(R.attr.mapAccentTextColor, R.attr.colorAccent)
            val accentTextColor = itemView.context.theme.obtainStyledAttributes(attrs).use { array ->
                // Return first resolved attribute or null
                if (array.indexCount > 0) array.getColorStateList(array.getIndex(0)) else null
            } ?: ColorStateList.valueOf(Color.BLACK) // fallback to black if no colorAccent is defined (unlikely)
            selector.isChecked = preference.isSelected(item)
            title.apply {
                if (item.titleRes != -1) setText(item.titleRes) else text = item.title
            }
            summary.apply {
                if (item.summaryRes != -1) setText(item.summaryRes) else text = item.summary
                isVisible = item.summaryRes != -1 || item.summary != null
            }
            badge.apply {
                if (item.badgeInfo != null) {
                    when {
                        item.badgeInfo.textRes != DEFAULT_RES_ID -> setText(item.badgeInfo.textRes)
                        else -> text = item.badgeInfo.text
                    }
                    isVisible = item.badgeInfo.isVisible
                } else {
                    isVisible = false
                }

                val badgeColor = item.badgeInfo?.badgeColor ?: accentTextColor
                setTextColor(badgeColor)
                backgroundTintList = badgeColor
                backgroundTintMode = PorterDuff.Mode.SRC_ATOP
            }
            itemView.setOnClickListener {
                if (preference.shouldSelect(item)) {
                    preference.select(item)
                    if (allowMultiSelect) notifyItemChanged(position)
                    else notifySelectionChanged()
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun notifySelectionChanged() {
        notifyItemRangeChanged(0, itemCount)
    }

    class SelectionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val selector: CompoundButton = itemView.findViewById(R.id.map_selector)
        val title: TextView = itemView.findViewById(android.R.id.title)
        val summary: TextView = itemView.findViewById(android.R.id.summary)
        val badge: TextView = itemView.findViewById(R.id.badge)
    }
}