SET DEFINE OFF;

PROMPT ============================================
PROMPT RENOMBRAR DIRECTOR_PROGRAMA -> DIRECTOR
PROMPT ============================================

DECLARE
    v_cnt NUMBER;
BEGIN
    SELECT COUNT(*)
      INTO v_cnt
      FROM user_tables
     WHERE table_name = 'DIRECTOR_PROGRAMA';

    IF v_cnt = 1 THEN
        EXECUTE IMMEDIATE 'RENAME director_programa TO director';
    END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE director RENAME CONSTRAINT pk_director_programa TO pk_director';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2443 THEN RAISE; END IF;
END;
/

DECLARE
    v_cnt NUMBER;
BEGIN
    SELECT COUNT(*)
      INTO v_cnt
      FROM user_sequences
     WHERE sequence_name = 'SEQ_DIRECTOR_PROGRAMA';

    IF v_cnt = 1 THEN
        EXECUTE IMMEDIATE 'RENAME seq_director_programa TO seq_director';
    END IF;
END;
/

DECLARE
    v_cnt NUMBER;
BEGIN
    SELECT COUNT(*)
      INTO v_cnt
      FROM user_triggers
     WHERE trigger_name = 'BI_DIRECTOR_PROGRAMA';

    IF v_cnt = 1 THEN
        BEGIN
            EXECUTE IMMEDIATE 'RENAME bi_director_programa TO bi_director';
        EXCEPTION
            WHEN OTHERS THEN
                IF SQLCODE != -4043 THEN RAISE; END IF;
        END;
    END IF;
END;
/

PROMPT ============================================
PROMPT VERIFICACION
PROMPT ============================================
SELECT tname, tabtype
FROM tab
WHERE tname IN ('DIRECTOR', 'DIRECTOR_PROGRAMA')
ORDER BY tname;
