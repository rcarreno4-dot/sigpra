SET DEFINE OFF;

PROMPT ==========================================
PROMPT Ajuste de roles segun reunion 2026-03-10
PROMPT ==========================================

PROMPT 1) Migrar valores antiguos de rol a nueva taxonomia
UPDATE usuario SET rol = 'TUTOR_ACADEMICO'   WHERE rol = 'DOCENTE';
UPDATE usuario SET rol = 'ASESOR_PEDAGOGICO' WHERE rol = 'INSTITUCION';
UPDATE usuario SET rol = 'DIRECTOR'          WHERE rol = 'SECRETARIA';
UPDATE usuario SET rol = 'TUTOR_ACADEMICO'   WHERE rol = 'ADMIN';

PROMPT 2) Reemplazar constraint de roles en tabla USUARIO
DECLARE
  v_count NUMBER := 0;
BEGIN
  SELECT COUNT(*)
    INTO v_count
    FROM user_constraints
   WHERE constraint_name = 'CK_USUARIO_ROL'
     AND table_name = 'USUARIO';

  IF v_count > 0 THEN
    EXECUTE IMMEDIATE 'ALTER TABLE usuario DROP CONSTRAINT ck_usuario_rol';
  END IF;
END;
/

ALTER TABLE usuario ADD CONSTRAINT ck_usuario_rol
  CHECK (rol IN ('ESTUDIANTE','TUTOR_ACADEMICO','DIRECTOR','ASESOR_PEDAGOGICO'));

COMMIT;

PROMPT Ajuste de roles completado.
