<?php

declare(strict_types=1);

require_once __DIR__ . '/includes/layout.php';

require_login(['Administrador']);

$usuarios = demo_users();

render_header('Gestion de Usuarios', 'usuarios.php', 'Registro y control de accesos por rol');
?>
<section class="grid-cols-2">
  <article class="panel">
    <h2>Formulario de usuario</h2>
    <form action="#" method="post">
      <label for="nombre">Nombre completo</label>
      <input id="nombre" name="nombre" type="text">

      <label for="correo">Correo</label>
      <input id="correo" name="correo" type="email">

      <label for="rol">Rol</label>
      <select id="rol" name="rol">
        <option>Administrador</option>
        <option>Docente</option>
        <option>Estudiante</option>
      </select>

      <label for="estado">Estado</label>
      <select id="estado" name="estado">
        <option>Activo</option>
        <option>En revision</option>
      </select>

      <div class="actions">
        <button class="btn btn-primary" type="submit">Guardar</button>
        <button class="btn btn-secondary" type="button">Actualizar</button>
      </div>
    </form>
  </article>

  <article class="panel">
    <h2>Listado de usuarios</h2>
    <div class="table-wrap" style="min-height: 380px;">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>Rol</th>
            <th>Estado</th>
          </tr>
        </thead>
        <tbody>
          <?php foreach ($usuarios as $u): ?>
            <tr>
              <td><?php echo (int) $u['id']; ?></td>
              <td><?php echo e($u['nombre']); ?></td>
              <td><?php echo e($u['rol']); ?></td>
              <td><span class="badge <?php echo e(estado_class((string) $u['estado'])); ?>"><?php echo e($u['estado']); ?></span></td>
            </tr>
          <?php endforeach; ?>
        </tbody>
      </table>
    </div>
  </article>
</section>
<?php
render_footer();
