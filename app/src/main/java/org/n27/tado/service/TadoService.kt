package org.n27.tado.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.n27.tado.Constants.LOGIN_RESPONSE
import org.n27.tado.R
import org.n27.tado.TadoApplication
import org.n27.tado.data.api.models.LoginResponse
import org.n27.tado.ui.login.LoginActivity
import javax.inject.Inject

class TadoService : LifecycleService() {

    private val notifId = 1
    private val notifChannelId = "channelId"

    private val scope = CoroutineScope(Dispatchers.Default)
    private var job: Job? = null

    @Inject lateinit var vm: TadoServiceViewModel
    private var loginResponse: LoginResponse? = null

    override fun onCreate() {
        (applicationContext as TadoApplication).appComponent.inject(this)
        super.onCreate()

        vm.accountDetails.observe(this, Observer { result ->
            val accountResult = result ?: return@Observer
            println(accountResult)
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        loginResponse = intent?.extras?.getSerializable(LOGIN_RESPONSE) as? LoginResponse
        startForeground()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        job?.cancel()
        super.onDestroy()
    }

    private fun startForeground() {
        val notificationIntent = Intent(this, LoginActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, FLAG_IMMUTABLE)

        createNotificationChannel()

        startForeground(notifId, NotificationCompat.Builder(this, notifChannelId)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_sync)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("Service is running background")
            .setContentIntent(pendingIntent)
            .build()
        )

        logic()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel.
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(notifChannelId, notifChannelId, importance)
        mChannel.description = notifChannelId
        // Register the channel with the system. You can't change the importance
        // or other notification behaviors after this.
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    private fun logic() {
        job?.cancel()
        job = scope.launch {
            //vm.turnHeatingOn(loginResponse?.access_token)

            while (true) {
                println("Hello")
                delay(timeMillis = 3000)
            }
        }
    }
}
