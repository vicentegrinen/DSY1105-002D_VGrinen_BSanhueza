package com.example.dsy1105_002d_vgrinen_bsanhueza.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import com.example.dsy1105_002d_vgrinen_bsanhueza.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenMediana() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Vista Mediana") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            Text("Tablets", style = MaterialTheme.typography.titleLarge)
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Leanchata Boss",
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}


@Preview(name = "Median", widthDp = 360, heightDp = 800)
@Composable
fun PreviewMedian() {
    HomeScreenMediana()
}