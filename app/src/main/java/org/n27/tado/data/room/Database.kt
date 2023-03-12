package org.n27.tado.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AcConfig::class], version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {
    abstract fun acConfigDao(): AcConfigDao
}