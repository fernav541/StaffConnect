package com.tecnm.staffconnect.presentation.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tecnm.staffconnect.presentation.auth.LoginScreen
import com.tecnm.staffconnect.presentation.Home.HomeScreen
import com.tecnm.staffconnect.presentation.auth.OlvidePasswordScreen
import com.tecnm.staffconnect.presentation.nominas.NominasScreen
import com.tecnm.staffconnect.presentation.vacaciones.VacacionesScreen
import com.tecnm.staffconnect.presentation.perfil.PerfilScreen

object Rutas {
    const val LOGIN = "login"
    const val HOME = "home"
    const val VACACIONES = "vacaciones"
    const val NOMINAS = "nominas"
    const val PERFIL = "perfil"

    const val OLVIDE_PASSWORD = "olvide_password"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN
    ) {
        composable(Rutas.LOGIN) {
            LoginScreen(
                onLoginExitoso = {
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                },
                onOlvidePassword = {
                    navController.navigate(Rutas.OLVIDE_PASSWORD)
                }
            )
        }

        composable(Rutas.OLVIDE_PASSWORD) {
            OlvidePasswordScreen(
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.HOME) {
            HomeScreen(
                onVerVacaciones = { navController.navigate(Rutas.VACACIONES) },
                onVerNominas = { navController.navigate(Rutas.NOMINAS) },
                onVerPerfil = { navController.navigate(Rutas.PERFIL) },
                onCerrarSesion = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.HOME) { inclusive = true }
                    }
                }
            )
        }
        composable(Rutas.VACACIONES) {
            VacacionesScreen(
                onVolver = { navController.popBackStack() }
            )
        }
        composable(Rutas.NOMINAS) {
            NominasScreen(
                onVolver = { navController.popBackStack() }
            )
        }
        composable(Rutas.PERFIL) {
            PerfilScreen(
                onVolver = { navController.popBackStack() }
            )
        }
    }
}