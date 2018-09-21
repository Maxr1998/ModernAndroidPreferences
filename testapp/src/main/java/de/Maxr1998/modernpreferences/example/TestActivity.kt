package de.Maxr1998.modernpreferences.example

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter

class TestActivity : AppCompatActivity(), PreferencesAdapter.OnScreenChangeListener {

    private val preferencesAdapter = PreferencesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferenceView = RecyclerView(this)
        setContentView(preferenceView)
        preferenceView.layoutManager = LinearLayoutManager(this)
        preferenceView.adapter = preferencesAdapter

        preferencesAdapter.setRootScreen(Common.createRootScreen(this))
        preferencesAdapter.onScreenChangeListener = this
    }

    override fun onScreenChanged(screen: PreferenceScreen, subScreen: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(subScreen)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
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