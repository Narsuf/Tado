package org.n27.tado.ui.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import org.n27.tado.Constants.PASSWORD
import org.n27.tado.Constants.TOKEN
import org.n27.tado.Constants.USERNAME
import org.n27.tado.TadoApplication
import org.n27.tado.databinding.ActivityLoginBinding
import org.n27.tado.service.TadoService
import org.n27.tado.ui.common.extensions.afterTextChanged
import org.n27.tado.ui.common.extensions.hideKeyboard
import org.n27.tado.ui.login.LoginState.Failure
import org.n27.tado.ui.login.LoginState.FormDataChanged
import org.n27.tado.ui.login.LoginState.Idle
import org.n27.tado.ui.login.LoginState.Success
import org.n27.tado.ui.main.MainActivity
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    @Inject lateinit var vm: LoginViewModel
    @Inject lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityLoginBinding

    private val myIntent by lazy { Intent(this, TadoService::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TadoApplication).appComponent.inject(this)

        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initObservers()
        binding.loginIfCredentialsSaved()
        binding.setUpViews()
    }

    private fun initObservers() { vm.viewState.observe(this, ::renderState) }

    private fun renderState(state: LoginState) = when (state) {
        Idle -> Unit
        is FormDataChanged -> formDataChanged(state)
        is Success -> navigateToMainActivity(state)
        is Failure -> showLoginFailed(state)
    }

    private fun formDataChanged(state: FormDataChanged) = with(binding) {
        login.isEnabled = state.isDataValid
        if (state.usernameError != null) username.error = getString(state.usernameError)
        if (state.passwordError != null) password.error = getString(state.passwordError)
    }

    private fun navigateToMainActivity(state: Success) {
        sharedPreferences.edit()
            .putString(USERNAME, binding.username.text.toString())
            .putString(PASSWORD, binding.password.text.toString())
            .apply()

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(TOKEN, state.result.access_token)

        startActivity(intent)
        finish()
    }

    private fun loading(isLoading: Boolean = true) = with(binding) {
        login.isEnabled = !isLoading
        loading.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showLoginFailed(state: Failure) {
        loading(false)
        Snackbar.make(binding.root, state.error.message ?: "Error", Snackbar.LENGTH_LONG).show()
    }

    private fun ActivityLoginBinding.setUpViews() {
        username.afterTextChanged { loginDataChanged() }

        password.apply {
            afterTextChanged { loginDataChanged() }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    IME_ACTION_DONE -> login()
                }

                false
            }
        }

        login.setOnClickListener {
            hideKeyboard(it)
            login()
        }

        //stopServiceButton.setOnClickListener { stopService(myIntent) }
    }

    private fun ActivityLoginBinding.loginDataChanged() {
        vm.loginDataChanged(username.text.toString(), password.text.toString())
    }

    private fun ActivityLoginBinding.login() {
        loading()
        vm.login(username.text.toString(), password.text.toString())
    }

    private fun ActivityLoginBinding.loginIfCredentialsSaved() {
        sharedPreferences.getString(USERNAME, null)?.let { usr ->
            username.setText(usr)
            sharedPreferences.getString(PASSWORD, null)?.let { psw ->
                password.setText(psw)
                login()
            }
        }
    }
}
