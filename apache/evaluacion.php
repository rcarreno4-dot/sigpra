<?php

declare(strict_types=1);

require_once __DIR__ . '/includes/layout.php';

require_login(['Administrador', 'Docente']);

$evaluaciones = demo_evaluations();
$practicas = demo_practices();

render_header('Evaluacion de practicas', 'evaluacion.php', 'Rubrica, nota y observaciones por estudiante');
?>
<section class="grid-cols-2">
  <article class="panel">
    <h2>Registrar evaluacion</h2>
    <form action="#" method="post">
      <label for="practica">Practica</label>
      <select id="practica" name="practica">
        <?php foreach ($practicas as $p): ?>
          <option>#<?php echo (int) $p['id']; ?> - <?php echo e($p['estudiante']); ?></option>
        <?php endforeach; ?>
      </select>

      <label for="rubrica">Criterio rubrica</label>
      <input id="rubrica" name="rubrica" type="text" value="Planeacion didactica">

      <label for="nota">Nota (0 a 5)</label>
      <input id="nota" name="nota" type="number" step="0.1" min="0" max="5" value="4.5">

      <label for="feedback">Retroalimentacion</label>
      <textarea id="feedback" name="feedback">Buen dominio del proceso pedagogico.</textarea>

      <div class="actions">
        <button class="btn btn-primary" type="button">Guardar evaluacion</button>
        <button class="btn btn-secondary" type="button">Actualizar</button>
      </div>
    </form>
  </article>

  <article class="panel">
    <h2>Historico de evaluaciones</h2>
    <div class="table-wrap" style="min-height: 380px;">
      <table>
        <thead>
          <tr>
            <th>Estudiante</th>
            <th>Rubrica</th>
            <th>Nota</th>
            <th>Observacion</th>
          </tr>
        </thead>
        <tbody>
          <?php foreach ($evaluaciones as $e): ?>
            <tr>
              <td><?php echo e($e['estudiante']); ?></td>
              <td><?php echo e($e['rubrica']); ?></td>
              <td><?php echo e($e['nota']); ?></td>
              <td><?php echo e($e['feedback']); ?></td>
            </tr>
          <?php endforeach; ?>
        </tbody>
      </table>
    </div>
  </article>
</section>
<?php
render_footer();
