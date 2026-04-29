# Prototipo Apache - Proyecto Integrador

Aplicacion web en PHP para visualizacion de vistas del proyecto en Apache.

## Estado actual

- Todas las vistas usan el mismo tema visual (coherente con inicio de sesion y panel estudiante).
- Logo institucional en forma de luna.
- Modo demo sin dependencia de base de datos para abrir rapido en Apache.

## Modulos incluidos

- `index.php`: inicio de sesion
- `admin.php`: panel administrador
- `docente.php`: panel docente
- `estudiante.php`: panel estudiante
- `usuarios.php`: gestion de usuarios
- `entidades.php`: gestion de entidades
- `practicas.php`: gestion de practicas
- `actividades.php`: actividades y evidencias
- `evaluacion.php`: evaluacion de practicas
- `reportes.php`: reportes consolidados

## Estructura

- `includes/bootstrap.php`: sesion, autenticacion y datos demo
- `includes/layout.php`: layout comun y navegacion
- `assets/css/styles.css`: estilos del tema
- `assets/js/app.js`: utilidades de UI

## Requisitos

- Apache + PHP 8.x

## Abrir en Apache

1. Copiar la carpeta `prototipo_apache` dentro de `htdocs` (XAMPP/WAMP).
2. Iniciar Apache.
3. Abrir: `http://localhost/prototipo_apache/index.php`

## Credenciales demo

- `admin@uni.edu` / `123456`
- `docente@uni.edu` / `123456`
- `estudiante@uni.edu` / `123456`
