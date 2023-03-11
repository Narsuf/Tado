package org.n27.tado.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import org.n27.tado.Constants.PASSWORD
import org.n27.tado.Constants.TOKEN
import org.n27.tado.R
import org.n27.tado.TadoApplication
import org.n27.tado.data.api.models.Mode
import org.n27.tado.databinding.ActivityMainBinding
import org.n27.tado.ui.login.LoginActivity
import org.n27.tado.ui.main.MainState.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    @Inject lateinit var sharedPreferences: SharedPreferences
    @Inject lateinit var vm: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TadoApplication).appComponent.inject(this)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.setUpViews()
        initObservers()

        supportActionBar?.title = "Scripts"

        val token = intent.extras?.getString(TOKEN)
        vm.getACs(token)
    }

    private fun ActivityMainBinding.setUpViews() {
        recyclerView.apply { layoutManager = LinearLayoutManager(context) }
    }

    private fun initObservers() { vm.viewState.observe(this, ::renderState) }

    private fun renderState(state: MainState) = when (state) {
        is Loading -> Unit
        is Success -> paintACs(state)
        is Failure -> showLoginFailed(state)
    }

    private fun paintACs(state: Success) {
        binding.loading.visibility = View.GONE
        binding.recyclerView.adapter = ACCardAdapter(
            state.acs,
            state.acsDetails,
            ::onIconClicked,
            ::onTemperatureClicked,
            ::onSwitchClicked
        )
    }

    private fun onIconClicked(mode: Mode) {
        Snackbar.make(binding.root, "Mode ${mode.name} selected", Snackbar.LENGTH_LONG).show()
    }

    private fun onTemperatureClicked(): Float {
        return 0f
    }

    private fun onSwitchClicked(isEnabled: Boolean) {
        Snackbar.make(binding.root, "Click", Snackbar.LENGTH_LONG).show()
    }

    private fun showLoginFailed(state: Failure) {
        binding.loading.visibility = View.GONE
        Snackbar.make(binding.root, state.error.message ?: "Error", Snackbar.LENGTH_LONG).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                logout()
                true
            }
            else -> false
        }
    }

    private fun logout() {
        val intent = Intent(this, LoginActivity::class.java)

        sharedPreferences.edit().remove(PASSWORD).apply()

        startActivity(intent)
        finish()
    }
}
