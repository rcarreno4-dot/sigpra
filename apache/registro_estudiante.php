<?php

declare(strict_types=1);

require_once __DIR__ . '/includes/layout.php';

if (is_post()) {
    $nombre = trim((string) ($_POST['nombre'] ?? ''));
    $correo = trim((string) ($_POST['correo'] ?? ''));
    $clave = (string) ($_POST['clave'] ?? '');
    $confirmacion = (string) ($_POST['confirmacion'] ?? '');

    if ($clave === '' || $confirmacion === '' || $clave !== $confirmacion) {
        set_flash('error', 'Las contrasenas no coinciden.');
        redirect('registro_estudiante.php');
    }

    if (register_student_user($nombre, $correo, $clave)) {
        set_flash('success', 'Registro exitoso. Ya puedes iniciar sesion con rol Estudiante.');
        redirect('index.php');
    }

    redirect('registro_estudiante.php');
}

render_header('Registro de estudiante', 'index.php', 'Creacion de cuenta local para acceso web.', false);
?>
<section class="login-layout">
  <article class="context-panel">
    <img class="context-logo" src="assets/img/sigpra-logo.jpeg?v=2" alt="Logo SIGPRA">
    <h2>Registro web</h2>
    <p>Este formulario crea una cuenta local para el prototipo Apache.</p>
    <ul>
      <li>- Rol asignado: Estudiante.</li>
      <li>- Estado inicial: Activo.</li>
      <li>- Podras ingresar desde la pantalla principal.</li>
    </ul>
  </article>

  <article class="form-panel">
    <h2>Crear cuenta</h2>
    <form action="registro_estudiante.php" method="post">
      <label for="nombre">Nombre completo</label>
      <input id="nombre" name="nombre" type="text" required>

      <label for="correo">Correo institucional</label>
      <input id="correo" name="correo" type="email" required>

      <label for="clave">Contrasena</label>
      <input id="clave" name="clave" type="password" required>

      <label for="confirmacion">Confirmar contrasena</label>
      <input id="confirmacion" name="confirmacion" type="password" required>

      <button class="btn btn-primary" type="submit">Registrar estudiante</button>
      <a class="btn btn-secondary" href="index.php">Volver a inicio</a>
    </form>
  </article>
</section>
<?php
render_footer();

