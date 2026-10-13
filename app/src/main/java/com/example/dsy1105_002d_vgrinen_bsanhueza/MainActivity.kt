package com.example.dsy1105_002d_vgrinen_bsanhueza

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.dsy1105_002d_vgrinen_bsanhueza.navegation.AppNavigation
import com.example.dsy1105_002d_vgrinen_bsanhueza.ui.theme.DSY1105002D_VGrinen_BSanhuezaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DSY1105002D_VGrinen_BSanhuezaTheme {
                Scaffold { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        AppNavigation()
                    }
                }
            }
        }
    }
}