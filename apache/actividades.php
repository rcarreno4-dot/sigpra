<?php

declare(strict_types=1);

require_once __DIR__ . '/includes/layout.php';

$user = require_login(['Administrador', 'Docente', 'Estudiante']);
$practicas = demo_practices();
$actividades = demo_activities();

if ($user['rol'] === 'Estudiante') {
    $actividades = array_values(array_filter(
        $actividades,
        static fn(array $a): bool => (int) $a['practica'] === 201
    ));
}

render_header('Actividades y evidencias', 'actividades.php', 'Carga y validacion de actividades de practica');
?>
<section class="grid-cols-2">
  <article class="panel">
    <h2>Registrar actividad</h2>
    <form action="#" method="post" enctype="multipart/form-data">
      <label for="practica">Practica</label>
      <select id="practica" name="practica">
        <?php foreach ($practicas as $p): ?>
          <option>#<?php echo (int) $p['id']; ?> - <?php echo e($p['estudiante']); ?></option>
        <?php endforeach; ?>
      </select>

      <label for="titulo">Descripcion actividad</label>
      <input id="titulo" name="titulo" type="text" value="Bitacora semanal">

      <label for="fecha">Fecha limite</label>
      <input id="fecha" name="fecha" type="date" value="2026-05-05">

      <label for="horas">Horas</label>
      <input id="horas" name="horas" type="number" min="1" max="12" value="2">

      <label for="estado">Estado</label>
      <select id="estado" name="estado">
        <option>Pendiente</option>
        <option>En revision</option>
        <option>Validada</option>
      </select>

      <label for="evidencia">Adjuntar evidencia</label>
      <input id="evidencia" name="evidencia" type="file">

      <button class="btn btn-primary" type="button">Guardar actividad</button>
    </form>
  </article>

  <article class="panel" id="evidencias">
    <h2>Listado de actividades</h2>
    <div class="table-wrap" style="min-height: 380px;">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Practica</th>
            <th>Tarea</th>
            <th>Fecha limite</th>
            <th>Estado</th>
          </tr>
        </thead>
        <tbody>
          <?php foreach ($actividades as $a): ?>
            <tr>
              <td>#<?php echo (int) $a['id']; ?></td>
              <td>#<?php echo (int) $a['practica']; ?></td>
              <td><?php echo e($a['titulo']); ?></td>
              <td><?php echo e($a['fecha_limite']); ?></td>
              <td><span class="badge <?php echo e(estado_class((string) $a['estado'])); ?>"><?php echo e($a['estado']); ?></span></td>
            </tr>
          <?php endforeach; ?>
        </tbody>
      </table>
    </div>
  </article>
</section>
<?php
render_footer();
