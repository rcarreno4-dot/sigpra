<?php

declare(strict_types=1);

require_once __DIR__ . '/includes/layout.php';

require_login(['Docente']);

$metricas = docente_metrics();
$actividades = demo_activities();

render_header('Panel de Docente', 'docente.php', 'Validacion de actividades y acompanamiento formativo');
?>
<section class="kpi-grid">
  <?php card_metric('Practicas asignadas', (string) $metricas['practicas_asignadas']); ?>
  <?php card_metric('Actividades pendientes', (string) $metricas['actividades_pendientes']); ?>
  <?php card_metric('Evaluaciones cerradas', (string) $metricas['evaluaciones_cerradas']); ?>
</section>

<section class="grid-cols-2">
  <article class="panel">
    <h2>Validar actividad</h2>
    <form action="#" method="post">
      <label for="actividad">Actividad</label>
      <select id="actividad" name="actividad">
        <?php foreach ($actividades as $a): ?>
          <option>#<?php echo (int) $a['id']; ?> - <?php echo e($a['titulo']); ?></option>
        <?php endforeach; ?>
      </select>

      <label for="estado">Estado</label>
      <select id="estado" name="estado">
        <option>Pendiente</option>
        <option>En revision</option>
        <option>Validada</option>
      </select>

      <label for="obs">Observaciones</label>
      <textarea id="obs" name="obs"></textarea>

      <button class="btn btn-primary" type="submit">Guardar validacion</button>
    </form>
  </article>

  <article class="panel">
    <h2>Listado de actividades</h2>
    <div class="table-wrap" style="min-height: 300px;">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Tarea</th>
            <th>Estado</th>
          </tr>
        </thead>
        <tbody>
          <?php foreach ($actividades as $a): ?>
            <tr>
              <td>#<?php echo (int) $a['id']; ?></td>
              <td><?php echo e($a['titulo']); ?></td>
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
