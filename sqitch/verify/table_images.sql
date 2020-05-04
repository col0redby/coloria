-- Verify coloria:table_images on mysql

BEGIN;

-- XXX Add verifications here.

USE coloria;

SELECT * FROM images limit 1;

ROLLBACK;
