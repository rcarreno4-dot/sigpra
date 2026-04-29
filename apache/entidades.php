<?php

declare(strict_types=1);

require_once __DIR__ . '/includes/layout.php';

require_login(['Administrador']);

$entidades = demo_entities();

render_header('Gestion de Entidades', 'entidades.php', 'Control de instituciones receptoras y cupos');
?>
<section class="grid-cols-2">
  <article class="panel">
    <h2>Formulario de entidad</h2>
    <form action="#" method="post">
      <label for="nombre">Nombre entidad</label>
      <input id="nombre" name="nombre" type="text">

      <label for="ciudad">Ciudad</label>
      <input id="ciudad" name="ciudad" type="text">

      <label for="cupos">Cupos disponibles</label>
      <input id="cupos" name="cupos" type="number" min="0">

      <label for="contacto">Telefono de contacto</label>
      <input id="contacto" name="contacto" type="text">

      <button class="btn btn-primary" type="submit">Guardar entidad</button>
    </form>
  </article>

  <article class="panel">
    <h2>Listado de entidades</h2>
    <div class="table-wrap" style="min-height: 380px;">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>Ciudad</th>
            <th>Cupos</th>
          </tr>
        </thead>
        <tbody>
          <?php foreach ($entidades as $e): ?>
            <tr>
              <td><?php echo (int) $e['id']; ?></td>
              <td><?php echo e($e['nombre']); ?></td>
              <td><?php echo e($e['ciudad']); ?></td>
              <td><?php echo (int) $e['cupos']; ?></td>
            </tr>
          <?php endforeach; ?>
        </tbody>
      </table>
    </div>
  </article>
</section>
<?php
render_footer();
