package com.example.inventory.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp
import com.example.inventory.R

@Composable
fun Logo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.logo_grupo),
        contentDescription = "Logo del grupo",
        contentScale = ContentScale.Fit,
        modifier = modifier.size(120.dp)
    )
}
