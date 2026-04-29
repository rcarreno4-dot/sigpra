<?php

declare(strict_types=1);

require_once __DIR__ . '/includes/layout.php';

require_login(['Estudiante']);

$metricas = estudiante_metrics();
$actividades = demo_activities();

render_header('Panel de Estudiante', 'estudiante.php', 'Seguimiento de practica academica y bitacora personal');
?>
<section class="kpi-grid">
  <?php card_metric('Horas registradas', (string) $metricas['horas_registradas']); ?>
  <?php card_metric('Horas validadas', (string) $metricas['horas_validadas']); ?>
  <?php card_metric('Estado practica', (string) $metricas['estado_practica']); ?>
</section>

<section class="board">
  <h2>Proximas tareas</h2>
  <div class="table-wrap">
    <table>
      <thead>
        <tr>
          <th>Tarea</th>
          <th>Fecha limite</th>
          <th>Estado</th>
        </tr>
      </thead>
      <tbody>
        <?php foreach ($actividades as $a): ?>
          <tr>
            <td><?php echo e($a['titulo']); ?></td>
            <td><?php echo e($a['fecha_limite']); ?></td>
            <td><span class="badge <?php echo e(estado_class((string) $a['estado'])); ?>"><?php echo e($a['estado']); ?></span></td>
          </tr>
        <?php endforeach; ?>
      </tbody>
    </table>
  </div>
</section>
<?php
render_footer();
