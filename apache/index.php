<?php

declare(strict_types=1);

require_once __DIR__ . '/includes/layout.php';

if (is_logged_in()) {
    $u = current_user();
    redirect(role_dashboard($u['rol']));
}

if (is_post()) {
    $correo = trim((string) ($_POST['correo'] ?? ''));
    $clave = (string) ($_POST['clave'] ?? '');
    $rol = trim((string) ($_POST['rol'] ?? ''));

    if ($correo === '' || $clave === '') {
        set_flash('error', 'Correo y contrasena son obligatorios.');
        redirect('index.php');
    }

    if (!login_user($correo, $clave, $rol)) {
        if (!isset($_SESSION['flash'])) {
            set_flash('error', 'Credenciales invalidas. Usa admin@uni.edu / 123456.');
        }
        redirect('index.php');
    }

    $auth = current_user();
    set_flash('success', 'Inicio de sesion exitoso.');
    redirect(role_dashboard($auth['rol']));
}

render_header('Inicio de sesion', 'index.php', '', false);
?>
<section class="login-layout">
  <article class="context-panel">
    <img class="context-logo" src="assets/img/sigpra-logo.jpeg?v=2" alt="Logo SIGPRA">
    <h2>Contexto del sistema</h2>
    <p>Plataforma para registrar, validar y hacer seguimiento de practicas academicas.</p>
    <ul>
      <li>- Registro publico: solo estudiantes.</li>
      <li>- Estudiante: registra practica, bitacora y evidencias.</li>
      <li>- Docente: valida actividades y horas.</li>
      <li>- Directora: registra docentes y aprueba cierre final.</li>
    </ul>
  </article>

  <article class="form-panel">
    <h2>Iniciar sesion</h2>
    <form action="index.php" method="post">
      <label for="correo">Correo institucional</label>
      <input id="correo" name="correo" type="email" required>

      <label for="clave">Contrasena</label>
      <input id="clave" name="clave" type="password" required>

      <label for="rol">Rol</label>
      <select id="rol" name="rol">
        <option>Estudiante</option>
        <option>Docente</option>
        <option>Administrador</option>
      </select>

      <button class="btn btn-primary" type="submit">Ingresar</button>
      <a class="btn btn-secondary" href="registro_estudiante.php">Registrarse como estudiante</a>
    </form>
  </article>
</section>
<?php
render_footer();
