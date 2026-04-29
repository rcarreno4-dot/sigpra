<?php

declare(strict_types=1);

if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

const APP_TITLE = 'SIGPRA';

function redirect(string $path): never
{
    header("Location: {$path}");
    exit;
}

function set_flash(string $type, string $message): void
{
    $_SESSION['flash'] = [
        'type' => $type,
        'message' => $message,
    ];
}

function pull_flash(): ?array
{
    if (!isset($_SESSION['flash'])) {
        return null;
    }

    $flash = $_SESSION['flash'];
    unset($_SESSION['flash']);
    return $flash;
}

function e(string|null $value): string
{
    return htmlspecialchars((string) $value, ENT_QUOTES, 'UTF-8');
}

function is_post(): bool
{
    return ($_SERVER['REQUEST_METHOD'] ?? 'GET') === 'POST';
}

function current_user(): ?array
{
    return $_SESSION['auth_user'] ?? null;
}

function is_logged_in(): bool
{
    return current_user() !== null;
}

function logout_user(): void
{
    unset($_SESSION['auth_user']);
}

function demo_users(): array
{
    $base = [
        ['id' => 1, 'nombre' => 'Coordinadora Ana Ruiz', 'correo' => 'admin@uni.edu', 'clave' => '123456', 'rol' => 'Administrador', 'estado' => 'Activo'],
        ['id' => 2, 'nombre' => 'Docente Luis Perez', 'correo' => 'docente@uni.edu', 'clave' => '123456', 'rol' => 'Docente', 'estado' => 'Activo'],
        ['id' => 3, 'nombre' => 'Estudiante Maria Gomez', 'correo' => 'estudiante@uni.edu', 'clave' => '123456', 'rol' => 'Estudiante', 'estado' => 'Activo'],
        ['id' => 4, 'nombre' => 'Estudiante Johan Diaz', 'correo' => 'johan@uni.edu', 'clave' => '123456', 'rol' => 'Estudiante', 'estado' => 'En revision'],
    ];

    return array_merge($base, registered_users());
}

function registered_users(): array
{
    $users = $_SESSION['registered_users'] ?? [];
    return is_array($users) ? $users : [];
}

function register_student_user(string $nombre, string $correo, string $clave): bool
{
    $nombre = trim($nombre);
    $correo = strtolower(trim($correo));
    $clave = trim($clave);

    if ($nombre === '' || $correo === '' || $clave === '') {
        set_flash('error', 'Completa nombre, correo y contrasena.');
        return false;
    }

    foreach (demo_users() as $user) {
        if (strcasecmp((string) $user['correo'], $correo) === 0) {
            set_flash('error', 'Ese correo ya existe en el sistema.');
            return false;
        }
    }

    $nextId = 1000 + count(registered_users()) + 1;
    $newUser = [
        'id' => $nextId,
        'nombre' => $nombre,
        'correo' => $correo,
        'clave' => $clave,
        'rol' => 'Estudiante',
        'estado' => 'Activo',
    ];

    $list = registered_users();
    $list[] = $newUser;
    $_SESSION['registered_users'] = $list;
    return true;
}

function demo_entities(): array
{
    return [
        ['id' => 101, 'nombre' => 'Colegio San Miguel', 'ciudad' => 'Bogota', 'cupos' => 12, 'contacto' => '3021112233'],
        ['id' => 102, 'nombre' => 'I.E. Nueva Esperanza', 'ciudad' => 'Medellin', 'cupos' => 8, 'contacto' => '3102223344'],
        ['id' => 103, 'nombre' => 'Colegio Prado Verde', 'ciudad' => 'Cali', 'cupos' => 5, 'contacto' => '3153334455'],
    ];
}

function demo_practices(): array
{
    return [
        ['id' => 201, 'estudiante' => 'Maria Gomez', 'docente' => 'Luis Perez', 'entidad' => 'Colegio San Miguel', 'horas' => 0, 'meta' => 120, 'estado' => 'SIN_REGISTRO'],
        ['id' => 202, 'estudiante' => 'Johan Diaz', 'docente' => 'Luis Perez', 'entidad' => 'I.E. Nueva Esperanza', 'horas' => 64, 'meta' => 120, 'estado' => 'En progreso'],
        ['id' => 203, 'estudiante' => 'Camila Torres', 'docente' => 'Daniel Rojas', 'entidad' => 'Colegio Prado Verde', 'horas' => 120, 'meta' => 120, 'estado' => 'Aprobada'],
    ];
}

function demo_activities(): array
{
    return [
        ['id' => 301, 'practica' => 201, 'titulo' => 'Registro inicial de practica', 'fecha_limite' => '2026-04-30', 'estado' => 'Pendiente'],
        ['id' => 302, 'practica' => 202, 'titulo' => 'Bitacora semanal', 'fecha_limite' => '2026-05-05', 'estado' => 'En revision'],
        ['id' => 303, 'practica' => 203, 'titulo' => 'Evidencia final', 'fecha_limite' => '2026-05-10', 'estado' => 'Validada'],
    ];
}

function demo_evaluations(): array
{
    return [
        ['estudiante' => 'Maria Gomez', 'rubrica' => 'Dominio disciplinar', 'nota' => '4.2', 'feedback' => 'Buen avance inicial'],
        ['estudiante' => 'Johan Diaz', 'rubrica' => 'Planeacion didactica', 'nota' => '4.8', 'feedback' => 'Planeacion clara y completa'],
    ];
}

function admin_metrics(): array
{
    $users = demo_users();
    $practices = demo_practices();

    $activos = 0;
    foreach ($users as $u) {
        if ($u['estado'] === 'Activo') {
            $activos++;
        }
    }

    $practicasActivas = 0;
    $sumHoras = 0;
    $sumPct = 0;

    foreach ($practices as $p) {
        if ($p['estado'] !== 'Aprobada') {
            $practicasActivas++;
        }

        $horas = (int) $p['horas'];
        $meta = (int) $p['meta'];
        $sumHoras += $horas;
        $sumPct += porcentaje($horas, $meta);
    }

    $count = max(1, count($practices));

    return [
        'usuarios' => $activos,
        'practicas_activas' => $practicasActivas,
        'promedio_horas' => (int) round($sumHoras / $count),
        'avance_global' => (int) round($sumPct / $count),
    ];
}

function docente_metrics(): array
{
    return [
        'practicas_asignadas' => 2,
        'actividades_pendientes' => 1,
        'evaluaciones_cerradas' => 1,
        'promedio_nota' => '4.5',
    ];
}

function estudiante_metrics(): array
{
    return [
        'horas_registradas' => 0,
        'horas_validadas' => 0,
        'estado_practica' => 'SIN_REGISTRO',
    ];
}

function report_metrics(): array
{
    return [
        'practicas_aprobadas' => 1,
        'promedio_notas' => '4.5',
        'entidades_activas' => 3,
        'alertas_pendientes' => 2,
    ];
}

function login_user(string $correo, string $clave, string $selectedRole = ''): bool
{
    foreach (demo_users() as $user) {
        if (strcasecmp($user['correo'], $correo) !== 0) {
            continue;
        }

        if ($user['clave'] !== $clave) {
            return false;
        }

        if ($user['estado'] !== 'Activo') {
            set_flash('error', 'Tu usuario no esta activo.');
            return false;
        }

        if ($selectedRole !== '' && $selectedRole !== $user['rol']) {
            set_flash('error', 'El rol seleccionado no coincide con tu cuenta.');
            return false;
        }

        $_SESSION['auth_user'] = [
            'id' => $user['id'],
            'nombre' => $user['nombre'],
            'correo' => $user['correo'],
            'rol' => $user['rol'],
        ];

        return true;
    }

    return false;
}

function role_dashboard(string $roleName): string
{
    return match ($roleName) {
        'Administrador' => 'admin.php',
        'Docente' => 'docente.php',
        default => 'estudiante.php',
    };
}

function require_login(array $roles = []): array
{
    $user = current_user();
    if ($user === null) {
        set_flash('error', 'Debes iniciar sesion para continuar.');
        redirect('index.php');
    }

    if ($roles !== [] && !in_array($user['rol'], $roles, true)) {
        set_flash('error', 'No tienes permisos para esta vista.');
        redirect(role_dashboard($user['rol']));
    }

    return $user;
}

function estado_class(string $estado): string
{
    return match (strtolower($estado)) {
        'activo', 'aprobada', 'validada' => 'ok',
        'en progreso', 'en revision', 'pendiente' => 'warn',
        'sin_registro' => 'neutral',
        default => 'neutral',
    };
}

function porcentaje(int $horas, int $meta): int
{
    if ($meta <= 0) {
        return 0;
    }

    return (int) min(100, round(($horas / $meta) * 100));
}
