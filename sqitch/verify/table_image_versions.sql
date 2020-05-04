-- Verify coloria:table_image_versions on mysql

BEGIN;

-- XXX Add verifications here.

USE coloria;

SELECT * FROM image_versions limit 1;

ROLLBACK;
