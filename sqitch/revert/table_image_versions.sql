-- Revert coloria:table_image_versions from mysql

BEGIN;

-- XXX Add DDLs here.

USE coloria;

DROP TABLE IF EXISTS image_versions;

COMMIT;
