<?php

declare(strict_types=1);

require_once __DIR__ . '/includes/layout.php';

require_login(['Administrador']);

$practicas = demo_practices();
$entidades = demo_entities();

render_header('Gestion de Practicas', 'practicas.php', 'Asignacion de estudiantes, docentes y entidades');
?>
<section class="grid-cols-2">
  <article class="panel">
    <h2>Asignar practica</h2>
    <form action="#" method="post">
      <label for="estudiante">Estudiante</label>
      <input id="estudiante" name="estudiante" type="text" value="Maria Gomez">

      <label for="docente">Docente asesor</label>
      <input id="docente" name="docente" type="text" value="Luis Perez">

      <label for="entidad">Entidad receptora</label>
      <select id="entidad" name="entidad">
        <?php foreach ($entidades as $e): ?>
          <option><?php echo e($e['nombre']); ?></option>
        <?php endforeach; ?>
      </select>

      <label for="meta">Meta de horas</label>
      <input id="meta" name="meta" type="number" value="120">

      <button class="btn btn-primary" type="submit">Guardar asignacion</button>
    </form>
  </article>

  <article class="panel">
    <h2>Seguimiento de practicas</h2>
    <div class="table-wrap" style="min-height: 380px;">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Estudiante</th>
            <th>Entidad</th>
            <th>Estado</th>
          </tr>
        </thead>
        <tbody>
          <?php foreach ($practicas as $p): ?>
            <tr>
              <td>#<?php echo (int) $p['id']; ?></td>
              <td><?php echo e($p['estudiante']); ?></td>
              <td><?php echo e($p['entidad']); ?></td>
              <td><span class="badge <?php echo e(estado_class((string) $p['estado'])); ?>"><?php echo e($p['estado']); ?></span></td>
            </tr>
          <?php endforeach; ?>
        </tbody>
      </table>
    </div>
  </article>
</section>
<?php
render_footer();
