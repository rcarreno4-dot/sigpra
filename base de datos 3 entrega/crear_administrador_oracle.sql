WHENEVER SQLERROR EXIT SQL.SQLCODE;
SET DEFINE OFF;

DECLARE
  v_count NUMBER := 0;
BEGIN
  SELECT COUNT(*)
    INTO v_count
    FROM DBA_PROFILES
   WHERE PROFILE = 'PERFIL_DBA_UNICO';

  IF v_count = 0 THEN
    EXECUTE IMMEDIATE q'[
      CREATE PROFILE perfil_dba_unico LIMIT
        SESSIONS_PER_USER 2
        FAILED_LOGIN_ATTEMPTS 5
        PASSWORD_LIFE_TIME 90
        PASSWORD_REUSE_TIME 365
        PASSWORD_REUSE_MAX 10
        PASSWORD_LOCK_TIME 1/24
    ]';
  END IF;
END;
/

DECLARE
  v_count NUMBER := 0;
BEGIN
  SELECT COUNT(*)
    INTO v_count
    FROM DBA_USERS
   WHERE USERNAME = 'ADMINISTRADOR';

  IF v_count = 0 THEN
    EXECUTE IMMEDIATE q'[
      CREATE USER administrador IDENTIFIED BY "administrador2026"
        DEFAULT TABLESPACE USERS
        TEMPORARY TABLESPACE TEMP
        PROFILE perfil_dba_unico
        ACCOUNT UNLOCK
    ]';
  ELSE
    BEGIN
      EXECUTE IMMEDIATE q'[
        ALTER USER administrador IDENTIFIED BY "administrador2026"
      ]';
    EXCEPTION
      WHEN OTHERS THEN
        IF SQLCODE != -28007 THEN
          RAISE;
        END IF;
    END;

    EXECUTE IMMEDIATE q'[
      ALTER USER administrador
        PROFILE perfil_dba_unico
        ACCOUNT UNLOCK
    ]';
  END IF;
END;
/

GRANT CREATE SESSION TO administrador;
GRANT DBA TO administrador;

EXIT;
