-- Revert coloria:table_images from mysql

BEGIN;

-- XXX Add DDLs here.

USE coloria;

DROP TABLE IF EXISTS images;

COMMIT;
