package de.Maxr1998.modernpreferences.example

import android.os.Bundle
import android.view.MenuItem
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter

abstract class BaseActivity : AppCompatActivity(), PreferencesAdapter.OnScreenChangeListener {

    protected lateinit var preferencesView: RecyclerView
    protected abstract val preferencesAdapter: PreferencesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesView = RecyclerView(this).apply {
            setContentView(this)
            layoutManager = object : LinearLayoutManager(this@BaseActivity) {
                override fun supportsPredictiveItemAnimations() = true
            }
            adapter = preferencesAdapter
            layoutAnimation = AnimationUtils.loadLayoutAnimation(this@BaseActivity, R.anim.preference_layout_fall_down)
        }
    }

    override fun onScreenChanged(screen: PreferenceScreen, subScreen: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(subScreen)
        preferencesView.scheduleLayoutAnimation()
        screen["25"]?.let { pref ->
            val viewOffset = ((preferencesView.height - 64 * resources.displayMetrics.density) / 2).toInt()
            (preferencesView.layoutManager as? LinearLayoutManager)
                    ?.scrollToPositionWithOffset(pref.screenPosition, viewOffset)
            pref.requestRebindAndHighlight()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (!preferencesAdapter.goBack())
            super.onBackPressed()
    }

    override fun onDestroy() {
        preferencesAdapter.onScreenChangeListener = null
        super.onDestroy()
    }
}