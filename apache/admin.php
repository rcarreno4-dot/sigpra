<?php

declare(strict_types=1);

require_once __DIR__ . '/includes/layout.php';

require_login(['Administrador']);

$metricas = admin_metrics();
$practicas = demo_practices();
$actividades = demo_activities();

render_header('Panel de Administrador', 'admin.php', 'Control general del sistema y seguimiento academico');
?>
<section class="kpi-grid">
  <?php card_metric('Usuarios activos', (string) $metricas['usuarios']); ?>
  <?php card_metric('Practicas activas', (string) $metricas['practicas_activas']); ?>
  <?php card_metric('Promedio de horas', (string) $metricas['promedio_horas']); ?>
</section>

<section class="board">
  <h2>Proximas tareas administrativas</h2>
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
