package org.n27.tado.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import org.n27.tado.Constants.LOGIN_RESPONSE
import org.n27.tado.TadoApplication
import org.n27.tado.data.api.models.LoginResponse
import org.n27.tado.databinding.ActivityLoginBinding
import org.n27.tado.service.TadoService
import org.n27.tado.ui.common.extensions.afterTextChanged
import org.n27.tado.ui.common.extensions.hideKeyboard
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    @Inject lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TadoApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = binding.username
        val password = binding.password
        val login = binding.login
        val loading = binding.loading

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer { result ->
            val loginResult = result ?: return@Observer

            loading.visibility = View.GONE

            loginResult
                .onSuccess { startService(it) }
                .onFailure { showLoginFailed(it.message ?: "Error") }
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }
        }

        login.setOnClickListener {
            loading.visibility = View.VISIBLE
            loginViewModel.login(username.text.toString(), password.text.toString())
            hideKeyboard(it)
        }
    }

    private fun startService(loginResponse: LoginResponse) {
        val myIntent = Intent(this, TadoService::class.java)
        myIntent.putExtra(LOGIN_RESPONSE, loginResponse)
        startService(myIntent)
    }

    private fun showLoginFailed(errorString: String) {
        Snackbar.make(binding.root, errorString, Snackbar.LENGTH_LONG).show()
    }
}
