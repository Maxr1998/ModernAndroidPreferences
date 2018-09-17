package de.Maxr1998.modernpreferences.example.view_model

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ViewModelTestActivity : AppCompatActivity() {

    var viewModel: MainViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val model = ViewModelProviders.of(this).get<MainViewModel>()

        val preferenceView = RecyclerView(this)
        setContentView(preferenceView)
        preferenceView.layoutManager = LinearLayoutManager(this)
        preferenceView.adapter = model.preferencesAdapter
        viewModel = model
    }

    override fun onBackPressed() {
        if (viewModel?.preferencesAdapter?.goBack() != true)
            super.onBackPressed()
    }
}