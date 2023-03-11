package org.n27.tado.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import org.n27.tado.Constants.PASSWORD
import org.n27.tado.Constants.USERNAME
import org.n27.tado.R
import org.n27.tado.TadoApplication
import org.n27.tado.databinding.ActivityMainBinding
import org.n27.tado.ui.login.LoginActivity
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    @Inject lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TadoApplication).appComponent.inject(this)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setUp()
    }

    private fun ActionBar.setUp() {
        title = "Scripts"
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
