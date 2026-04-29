<?php

declare(strict_types=1);

require_once __DIR__ . '/bootstrap.php';

function nav_items(): array
{
    $user = current_user();
    if ($user === null) {
        return ['index.php' => 'Login'];
    }

    return match ($user['rol']) {
        'Administrador' => [
            'admin.php' => 'Dashboard',
            'usuarios.php' => 'Usuarios',
            'entidades.php' => 'Entidades',
            'practicas.php' => 'Practicas',
            'actividades.php' => 'Actividades',
            'evaluacion.php' => 'Evaluacion',
            'reportes.php' => 'Reportes',
            'logout.php' => 'Cerrar sesion',
        ],
        'Docente' => [
            'docente.php' => 'Dashboard',
            'actividades.php' => 'Registro',
            'evaluacion.php' => 'Bitacora',
            'reportes.php' => 'Evidencias',
            'logout.php' => 'Cerrar sesion',
        ],
        default => [
            'estudiante.php' => 'Dashboard',
            'actividades.php' => 'Registro',
            'reportes.php' => 'Bitacora',
            'actividades.php#evidencias' => 'Evidencias',
            'logout.php' => 'Cerrar sesion',
        ],
    };
}

function render_header(string $title, string $active, string $subtitle = '', bool $showAppHeader = true): void
{
    $items = nav_items();
    $user = current_user();
    $flash = pull_flash();
    ?>
<!doctype html>
<html lang="es">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><?php echo e(APP_TITLE . ' - ' . $title); ?></title>
  <link rel="stylesheet" href="assets/css/styles.css">
</head>
<body>
  <div class="window-bar">
    <img class="app-logo" src="assets/img/sigpra-logo.jpeg?v=2" alt="Logo SIGPRA">
    <span class="window-title"><?php echo e(APP_TITLE . ' - ' . $title); ?></span>
  </div>

  <main class="app-shell">
    <?php if ($showAppHeader): ?>
      <section class="page-heading">
        <div>
          <h1><?php echo e($title); ?></h1>
          <?php if ($subtitle !== ''): ?>
            <p><?php echo e($subtitle); ?></p>
          <?php endif; ?>
        </div>
        <?php if ($user !== null): ?>
          <div class="role-pill">Rol activo: <?php echo e($user['rol']); ?></div>
        <?php endif; ?>
      </section>

      <nav class="tab-nav">
        <?php foreach ($items as $file => $label): ?>
          <?php $isActive = $active === $file ? 'active' : ''; ?>
          <a class="tab <?php echo e($isActive); ?>" href="<?php echo e($file); ?>"><?php echo e($label); ?></a>
        <?php endforeach; ?>
      </nav>
    <?php endif; ?>

    <?php if ($flash !== null): ?>
      <div class="alert <?php echo e('alert-' . $flash['type']); ?>"><?php echo e($flash['message']); ?></div>
    <?php endif; ?>
<?php
}

function render_footer(): void
{
    ?>
  </main>
  <script src="assets/js/app.js"></script>
</body>
</html>
<?php
}

function card_metric(string $label, string $valor): void
{
    ?>
    <article class="kpi-card">
      <h3><?php echo e($label); ?></h3>
      <p><?php echo e($valor); ?></p>
    </article>
    <?php
}
