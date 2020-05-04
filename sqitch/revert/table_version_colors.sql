-- Revert coloria:table_version_colors from mysql

BEGIN;

-- XXX Add DDLs here.

USE coloria;

DROP TABLE IF EXISTS version_colors;

COMMIT;
