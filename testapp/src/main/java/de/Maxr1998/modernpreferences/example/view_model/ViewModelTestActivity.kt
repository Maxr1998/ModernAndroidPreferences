package de.Maxr1998.modernpreferences.example.view_model

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter

class ViewModelTestActivity : AppCompatActivity(), PreferencesAdapter.OnScreenChangeListener {

    private lateinit var viewModel: MainViewModel
    private val preferencesAdapter get() = viewModel.preferencesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get<MainViewModel>()

        val preferenceView = RecyclerView(this)
        setContentView(preferenceView)
        val layoutManager = LinearLayoutManager(this)
        preferenceView.layoutManager = layoutManager
        preferenceView.adapter = preferencesAdapter

        preferencesAdapter.restoreAndObserveScrollPosition(preferenceView)
        onScreenChanged(preferencesAdapter.currentScreen, preferencesAdapter.isInSubScreen())
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
        if (!viewModel.preferencesAdapter.goBack())
            super.onBackPressed()
    }

    override fun onDestroy() {
        preferencesAdapter.onScreenChangeListener = null
        super.onDestroy()
    }
}