package org.n27.tado.injection

import dagger.Component
import org.n27.tado.data.api.injection.NetModule
import org.n27.tado.service.TadoService
import org.n27.tado.ui.login.LoginActivity
import javax.inject.Singleton


@Singleton
@Component(
    modules = [
        NetModule::class
    ]
)
interface AppComponent {

    fun inject(activity: LoginActivity)
    fun inject(service: TadoService)
}
