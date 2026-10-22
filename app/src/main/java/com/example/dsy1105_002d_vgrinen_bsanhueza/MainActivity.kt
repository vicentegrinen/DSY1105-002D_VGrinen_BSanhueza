package com.example.dsy1105_002d_vgrinen_bsanhueza

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.dsy1105_002d_vgrinen_bsanhueza.data.AppDatabase
import com.example.dsy1105_002d_vgrinen_bsanhueza.data.PreferencesManager
import com.example.dsy1105_002d_vgrinen_bsanhueza.navegation.AppNavigation
import com.example.dsy1105_002d_vgrinen_bsanhueza.repository.CuentaRepository
import com.example.dsy1105_002d_vgrinen_bsanhueza.repository.UsuarioRepository
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.theme.DSY1105002D_VGrinen_BSanhuezaTheme
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.UsuarioViewModel
import com.example.dsy1105_002d_vgrinen_bsanhueza.viewmodel.UsuarioViewModelFactory

class MainActivity : ComponentActivity() {

    // Se inicializan en onCreate: base de datos ROOM, DataStore, repositorios y factory
    private lateinit var database: AppDatabase
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var repository: UsuarioRepository
    private lateinit var cuentaRepository: CuentaRepository

    private val usuarioViewModel: UsuarioViewModel by viewModels {
        UsuarioViewModelFactory(repository, preferencesManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = AppDatabase.getDatabase(applicationContext)
        preferencesManager = PreferencesManager(applicationContext)
        repository = UsuarioRepository(database.usuarioDao())
        cuentaRepository = CuentaRepository(database.cuentaDao())

        enableEdgeToEdge()
        setContent {
            DSY1105002D_VGrinen_BSanhuezaTheme {
                Scaffold { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        AppNavigation(usuarioViewModel, cuentaRepository)
                    }
                }
            }
        }
    }
}
