package org.n27.tado.injection

import android.app.Application
import android.content.SharedPreferences
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme
import androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme
import androidx.security.crypto.MasterKey
import dagger.Module
import dagger.Provides
import org.n27.tado.Utils
import org.n27.tado.data.room.Database
import javax.inject.Singleton

@Module
class AppModule(val app: Application) {

    @Provides
    @Singleton
    fun provideApplication() = app

    @Provides
    @Singleton
    fun provideDatabase(app: Application) = Room
        .databaseBuilder(app, Database::class.java, "tado_db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideAcConfigDao(database: Database) = database.acConfigDao()

    @Provides
    @Singleton
    fun provideMasterKey() = MasterKey.Builder(app)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    @Provides
    @Singleton
    fun provideEncryptedSharedPreferences(masterKey: MasterKey): SharedPreferences = EncryptedSharedPreferences.create(
        app,
        "secret_shared_prefs",
        masterKey,
        PrefKeyEncryptionScheme.AES256_SIV,
        PrefValueEncryptionScheme.AES256_GCM
    )

    @Provides
    @Singleton
    fun provideUtils() = Utils()
}
