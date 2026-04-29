<?php

declare(strict_types=1);

require_once __DIR__ . '/includes/layout.php';

$user = require_login(['Administrador', 'Docente', 'Estudiante']);
$practicas = demo_practices();
$metricas = report_metrics();

if ($user['rol'] === 'Estudiante') {
    $practicas = array_values(array_filter(
        $practicas,
        static fn(array $p): bool => $p['estudiante'] === 'Maria Gomez'
    ));
}

render_header('Reportes', 'reportes.php', 'Consolidado para coordinacion y director de programa');
?>
<section class="kpi-grid">
  <?php card_metric('Practicas aprobadas', (string) $metricas['practicas_aprobadas']); ?>
  <?php card_metric('Promedio notas', (string) $metricas['promedio_notas']); ?>
  <?php card_metric('Entidades activas', (string) $metricas['entidades_activas']); ?>
</section>

<section class="grid-cols-2">
  <article class="panel">
    <h2>Resumen por estudiante</h2>
    <div class="table-wrap" style="min-height: 380px;">
      <table>
        <thead>
          <tr>
            <th>Estudiante</th>
            <th>Horas</th>
            <th>Estado</th>
            <th>Avance</th>
          </tr>
        </thead>
        <tbody>
          <?php foreach ($practicas as $p): ?>
            <?php $avance = porcentaje((int) $p['horas'], (int) $p['meta']); ?>
            <tr>
              <td><?php echo e($p['estudiante']); ?></td>
              <td><?php echo (int) $p['horas']; ?>/<?php echo (int) $p['meta']; ?></td>
              <td><span class="badge <?php echo e(estado_class((string) $p['estado'])); ?>"><?php echo e($p['estado']); ?></span></td>
              <td>
                <div class="progress"><span style="width: <?php echo (int) $avance; ?>%;"></span></div>
                <small class="helper"><?php echo (int) $avance; ?>%</small>
              </td>
            </tr>
          <?php endforeach; ?>
        </tbody>
      </table>
    </div>
  </article>

  <article class="panel">
    <h2>Exportar reporte</h2>
    <p class="helper">Vista lista para conectar exportacion PDF y Excel.</p>
    <form action="#" method="post">
      <label for="tipo">Tipo de reporte</label>
      <select id="tipo" name="tipo">
        <option>Consolidado general</option>
        <option>Por entidad receptora</option>
        <option>Por docente asesor</option>
        <option>Por estudiante</option>
      </select>

      <label for="fecha_ini">Fecha inicial</label>
      <input id="fecha_ini" name="fecha_ini" type="date">

      <label for="fecha_fin">Fecha final</label>
      <input id="fecha_fin" name="fecha_fin" type="date">

      <div class="actions">
        <button class="btn btn-primary" type="button">Generar PDF</button>
        <button class="btn btn-secondary" type="button">Generar Excel</button>
      </div>
    </form>
  </article>
</section>
<?php
render_footer();
