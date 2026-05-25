package com.tecnm.staffconnect.presentation.vacaciones

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VacacionesScreen(
    onVolver: () -> Unit,
    viewModel: VacacionesViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var fechaInicio by remember { mutableStateOf("") }
    var fechaFin by remember { mutableStateOf("") }
    var motivo by remember { mutableStateOf("") }
    val vacaciones by viewModel.vacaciones.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun mostrarDatePicker(onFechaSeleccionada: (String) -> Unit) {
        val calendario = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, anio, mes, dia ->
                val fechaFormateada = "%02d/%02d/%04d".format(dia, mes + 1, anio)
                onFechaSeleccionada(fechaFormateada)
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun validarYEnviar() {
        when {
            fechaInicio.isEmpty() -> {
                scope.launch {
                    snackbarHostState.showSnackbar("Selecciona la fecha de inicio")
                }
            }
            fechaFin.isEmpty() -> {
                scope.launch {
                    snackbarHostState.showSnackbar("Selecciona la fecha de fin")
                }
            }
            motivo.isEmpty() -> {
                scope.launch {
                    snackbarHostState.showSnackbar("Escribe el motivo de la solicitud")
                }
            }
            else -> {
                viewModel.solicitarVacacion(fechaInicio, fechaFin, motivo)
                fechaInicio = ""
                fechaFin = ""
                motivo = ""
                scope.launch {
                    snackbarHostState.showSnackbar("✓ Solicitud enviada correctamente")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitud de Vacaciones") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // DEBUG TEMPORAL — borra después de verificar
            Text(
                text = "UserID actual: ${viewModel.getUserIdDebug()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )

            OutlinedTextField(
                value = fechaInicio,
                onValueChange = {},
                label = { Text("Fecha inicio") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { mostrarDatePicker { fechaInicio = it } }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                    }
                }
            )

            OutlinedTextField(
                value = fechaFin,
                onValueChange = {},
                label = { Text("Fecha fin") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { mostrarDatePicker { fechaFin = it } }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                    }
                }
            )

            OutlinedTextField(
                value = motivo,
                onValueChange = { motivo = it },
                label = { Text("Motivo") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Button(
                onClick = { validarYEnviar() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enviar solicitud")
            }

            if (vacaciones.isNotEmpty()) {
                Text(
                    text = "Mis solicitudes",
                    style = MaterialTheme.typography.titleMedium
                )
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(vacaciones) { vacacion ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "${vacacion.fechaInicio} → ${vacacion.fechaFin}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = vacacion.motivo,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                val colorEstado = when (vacacion.estado.uppercase()) {
                                    "APROBADO" -> MaterialTheme.colorScheme.primaryContainer
                                    "RECHAZADO" -> MaterialTheme.colorScheme.errorContainer
                                    else -> MaterialTheme.colorScheme.secondaryContainer
                                }
                                val colorTexto = when (vacacion.estado.uppercase()) {
                                    "APROBADO" -> MaterialTheme.colorScheme.onPrimaryContainer
                                    "RECHAZADO" -> MaterialTheme.colorScheme.onErrorContainer
                                    else -> MaterialTheme.colorScheme.onSecondaryContainer
                                }
                                Surface(
                                    color = colorEstado,
                                    shape = MaterialTheme.shapes.small
                                ) {
                                    Text(
                                        text = vacacion.estado,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = colorTexto
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Sin solicitudes",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Aún no has solicitado vacaciones",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}