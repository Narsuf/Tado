package org.n27.tado.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.n27.tado.data.api.models.Mode

@Entity(tableName = "ac_config")
data class AcConfig(
    @PrimaryKey val id: Int,
    val name: String,
    val mode: Mode,
    val temperature: Float,
    val serviceEnabled: Boolean
)