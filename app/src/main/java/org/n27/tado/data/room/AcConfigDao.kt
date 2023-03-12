package org.n27.tado.data.room

import androidx.room.*
import org.n27.tado.data.api.models.Mode

@Dao
interface AcConfigDao {

    @Transaction
    @Query("SELECT * FROM ac_config")
    suspend fun getAcConfigs(): List<AcConfig>

    @Transaction
    @Query("SELECT * FROM ac_config WHERE id = :id")
    suspend fun getAcConfig(id: Int): AcConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAcConfig(acConfig: AcConfig)
}