package com.tecnm.staffconnect.presentation.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun ErrorSnackbar(
    mensaje: String,
    onDismiss: () -> Unit
) {
    Snackbar(
        action = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    ) {
        Text(mensaje)
    }
}

@Composable
fun LoadingDialog() {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {},
        title = { Text("Cargando...") },
        text = {
            CircularProgressIndicator()
        }
    )
}