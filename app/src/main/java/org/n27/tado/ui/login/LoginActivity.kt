package org.n27.tado.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
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

    @Inject lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    private val myIntent by lazy { Intent(this, TadoService::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TadoApplication).appComponent.inject(this)

        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initObservers()
        binding.setUpViews()
    }

    private fun ActivityLoginBinding.setUpViews() {
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
                    IME_ACTION_DONE ->
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
            hideKeyboard(it)
            loginViewModel.login(username.text.toString(), password.text.toString())
        }

        //stopServiceButton.setOnClickListener { stopService(myIntent) }
    }

    private fun initObservers() {
        loginViewModel.viewState.observe(this, ::renderState)
    }

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
        binding.loading.visibility = View.GONE
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showLoginFailed(state: Failure) {
        binding.loading.visibility = View.GONE
        Snackbar.make(binding.root, state.error.message ?: "Error", Snackbar.LENGTH_LONG).show()
    }
}
