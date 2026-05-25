# StaffConnect — Portal de Autoservicio HR Tech

Aplicación móvil empresarial desarrollada para el Tecnológico Nacional de México,
Instituto Tecnológico de León.

## Descripción

StaffConnect centraliza las funciones de autoservicio para empleados de pequeñas
y medianas empresas, permitiendo gestionar solicitudes de vacaciones, consultar
nóminas y administrar el perfil personal.

## Stack Tecnológico

- **Lenguaje**: Kotlin 100%
- **UI**: Jetpack Compose (sin XML)
- **Arquitectura**: MVVM + Clean Architecture
- **Inyección de dependencias**: Hilt
- **Base de datos local**: Room (modo offline)
- **Red**: Retrofit + OkHttp con interceptores de seguridad
- **Asincronía**: Coroutines + StateFlow
- **Seguridad**: DataStore para tokens y sesiones

## Funcionalidades

- Login con validación de credenciales contra API REST
- Gestión de solicitudes de vacaciones con selector de fecha
- Repositorio de nóminas con desglose expandible
- Perfil de empleado con datos reales
- Modo offline — Room mantiene datos sin internet
- Recuperación de contraseña

## Arquitectura