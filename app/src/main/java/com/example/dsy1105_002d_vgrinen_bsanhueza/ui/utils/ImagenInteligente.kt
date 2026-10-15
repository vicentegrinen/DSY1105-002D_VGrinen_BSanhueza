package com.example.dsy1105_002d_vgrinen_bsanhueza.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ImagenInteligente(
    uri: Uri?,
    modifier: Modifier = Modifier,
    size: Dp = 160.dp
) {
    val base = modifier
        .size(size)
        .clip(CircleShape)

    if (uri != null) {
        AsyncImage(
            model = uri,
            contentDescription = "Foto de perfil",
            contentScale = ContentScale.Crop,
            modifier = base
        )
    } else {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Sin foto de perfil",
            modifier = base,
            tint = MaterialTheme.colorScheme.outline
        )
    }
}