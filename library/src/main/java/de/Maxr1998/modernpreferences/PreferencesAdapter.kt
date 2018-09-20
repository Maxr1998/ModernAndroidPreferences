package de.Maxr1998.modernpreferences

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import de.Maxr1998.modernpreferences.helpers.emptyScreen
import de.Maxr1998.modernpreferences.preferences.CategoryHeader
import java.util.*

class PreferencesAdapter : RecyclerView.Adapter<PreferencesAdapter.ViewHolder>() {

    private val screenStack: Stack<PreferenceScreen> = Stack<PreferenceScreen>().apply {
        push(emptyScreen)
    }

    val currentScreen: PreferenceScreen
        get() = screenStack.peek()

    var secondScreenAdapter: PreferencesAdapter? = null

    fun setRootScreen(root: PreferenceScreen) {
        while (screenStack.peek() != emptyScreen) {
            screenStack.pop()
        }
        screenStack.push(root)
        notifyDataSetChanged()
    }

    private fun openScreen(screen: PreferenceScreen) {
        secondScreenAdapter?.setRootScreen(screen) ?: /* ELSE */ run {
            screenStack.push(screen)
            notifyDataSetChanged()
        }
    }

    fun goBack(): Boolean {
        // Check if second screen can still go back
        if (secondScreenAdapter?.goBack() == true)
            return true

        // Remove current screen from stack if more than root and empty screen are on it
        if (screenStack.size > 2) {
            screenStack.pop()
            notifyDataSetChanged()
            return true
        }
        return false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val layout = if (viewType == CategoryHeader.RESOURCE_CONST) R.layout.preference_category else R.layout.preference
        val view = layoutInflater.inflate(layout, parent, false)
        if (viewType > 0)
            layoutInflater.inflate(viewType, view.findViewById(R.id.widget_frame), true)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pref = currentScreen[position]
        pref.bindViews(holder)

        holder.itemView.setOnClickListener {
            // Item was clicked, check enabled state (not for PreferenceScreens) and send click event
            if (pref is PreferenceScreen) {
                openScreen(pref)
            } else if (!pref.enabled) return@setOnClickListener

            pref.performClick(holder)
        }
    }

    override fun getItemCount() = currentScreen.size()

    @LayoutRes
    override fun getItemViewType(position: Int) = currentScreen[position].getWidgetLayoutResource()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconFrame: View? = itemView.findViewById(R.id.icon_frame)
        val icon: ImageView? = itemView.findViewById(android.R.id.icon)
        val title: TextView = itemView.findViewById(android.R.id.title)
        val summary: TextView? = itemView.findViewById(android.R.id.summary)
        val widget: View? = itemView.findViewById<ViewGroup>(R.id.widget_frame)?.getChildAt(0)
    }
}