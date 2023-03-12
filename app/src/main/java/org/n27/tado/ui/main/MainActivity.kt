package org.n27.tado.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import org.n27.tado.Constants.PASSWORD
import org.n27.tado.Constants.TOKEN
import org.n27.tado.R
import org.n27.tado.TadoApplication
import org.n27.tado.data.api.models.Mode
import org.n27.tado.databinding.ActivityMainBinding
import org.n27.tado.service.TadoService
import org.n27.tado.ui.login.LoginActivity
import org.n27.tado.ui.main.MainState.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    @Inject lateinit var sharedPreferences: SharedPreferences
    @Inject lateinit var vm: MainViewModel
    private val myIntent by lazy { Intent(this, TadoService::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TadoApplication).appComponent.inject(this)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.setUpViews()
        initObservers()

        supportActionBar?.title = "Scripts"

        val token = intent.extras?.getString(TOKEN)
        vm.getAcsConfigs(token)
    }

    private fun ActivityMainBinding.setUpViews() {
        recyclerView.apply { layoutManager = LinearLayoutManager(context) }
    }

    private fun initObservers() { vm.viewState.observe(this, ::renderState) }

    private fun renderState(state: MainState) = when (state) {
        Loading -> Unit
        ConfigUpdated -> vm.getAcsConfigsFromDb()
        is Success -> paintACs(state)
        is Failure -> showLoginFailed(state)
        is DbConfigRetrieved -> stopService(state)
    }

    private fun paintACs(state: Success) {
        binding.loading.visibility = View.GONE
        binding.recyclerView.adapter = ACCardAdapter(
            state.acsConfigs,
            ::onIconClicked,
            ::onTemperatureClicked,
            ::onSwitchClicked
        )
    }

    private fun onIconClicked(id: Int, mode: Mode) { vm.postConfigChanges(id, mode = mode) }

    private fun onTemperatureClicked(id: Int, callback: OnTemperatureUpdated) {
        val builder = AlertDialog.Builder(this)
            .setTitle("Desired temperature")

        val numberPicker = NumberPicker(this).apply {
            minValue = 16
            maxValue = 30
        }

        builder
            .setView(numberPicker)
            .setPositiveButton("OK") { _, _ ->
                val temperature = numberPicker.value.toFloat()
                vm.postConfigChanges(id, temperature = temperature)
                callback("$temperature º")
            }.show()
    }

    private fun onSwitchClicked(id: Int, isEnabled: Boolean) {
        vm.postConfigChanges(id, serviceEnabled = isEnabled)

        if (isEnabled) startService(myIntent)
    }

    private fun showLoginFailed(state: Failure) {
        binding.loading.visibility = View.GONE
        Snackbar.make(binding.root, state.error.message ?: "Error", Snackbar.LENGTH_LONG).show()
    }

    private fun stopService(state: DbConfigRetrieved) {
        var shouldStopService = true

        state.acsConfigs.forEach { if (it.serviceEnabled) shouldStopService = false }

        if (shouldStopService) stopService(myIntent)
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
