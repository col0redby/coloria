-- Verify coloria:table_version_colors on mysql

BEGIN;

-- XXX Add verifications here.

USE coloria;

SELECT * FROM version_colors limit 1;

ROLLBACK;
