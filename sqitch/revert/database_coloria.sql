-- Revert coloria:database_coloria from mysql

BEGIN;

-- XXX Add DDLs here.

DROP DATABASE IF EXISTS coloria;

COMMIT;
