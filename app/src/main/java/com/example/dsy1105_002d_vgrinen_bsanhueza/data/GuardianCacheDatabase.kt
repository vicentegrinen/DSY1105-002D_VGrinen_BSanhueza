package com.example.dsy1105_002d_vgrinen_bsanhueza.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Base de datos local de Modo Guardián (copia de eventos, cámaras e indicadores).
// Es un archivo aparte de AppDatabase ("app_db") a propósito: así no cambia su versión
// ni se pierden las cuentas y registros que ya guarda la app.
@Database(
    entities = [
        EventoCacheEntity::class,
        CamaraCacheEntity::class,
        IndicadoresCacheEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GuardianCacheDatabase : RoomDatabase() {

    abstract fun cacheDao(): GuardianCacheDao

    companion object {
        @Volatile
        private var INSTANCE: GuardianCacheDatabase? = null

        fun getDatabase(context: Context): GuardianCacheDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    GuardianCacheDatabase::class.java,
                    "guardian_cache_db"
                )
                    // Es solo una copia: si cambia el esquema se puede recrear sin perder nada
                    .fallbackToDestructiveMigration(true)
                    .build().also { INSTANCE = it }
            }
    }
}
