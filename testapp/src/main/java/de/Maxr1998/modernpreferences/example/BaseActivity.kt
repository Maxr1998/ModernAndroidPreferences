package de.Maxr1998.modernpreferences.example

import android.os.Bundle
import android.view.MenuItem
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.example.databinding.ActivityMainBinding

abstract class BaseActivity : AppCompatActivity(), PreferencesAdapter.OnScreenChangeListener {

    protected abstract val preferencesAdapter: PreferencesAdapter
    protected lateinit var preferencesView: RecyclerView
    private lateinit var binding: ActivityMainBinding
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        layoutManager = LinearLayoutManager(this)
        preferencesView = binding.recyclerView.apply {
            layoutManager = this@BaseActivity.layoutManager
            adapter = preferencesAdapter
            layoutAnimation = AnimationUtils.loadLayoutAnimation(this@BaseActivity, R.anim.preference_layout_fall_down)
        }
    }

    override fun onScreenChanged(screen: PreferenceScreen, subScreen: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(subScreen)
        preferencesView.scheduleLayoutAnimation()
        screen["25"]?.let { pref ->
            val viewOffset = ((preferencesView.height - 64 * resources.displayMetrics.density) / 2).toInt()
            layoutManager.scrollToPositionWithOffset(pref.screenPosition, viewOffset)
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
        preferencesView.adapter = null
        super.onDestroy()
    }
}