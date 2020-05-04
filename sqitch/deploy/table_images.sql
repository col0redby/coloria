-- Deploy coloria:table_images to mysql
-- requires: database_coloria

BEGIN;

-- XXX Add DDLs here.

USE coloria;

CREATE TABLE IF NOT EXISTS images
(
  id            MEDIUMINT AUTO_INCREMENT PRIMARY KEY,
  title         VARCHAR(200) DEFAULT 'test_image',
  description   VARCHAR(200) DEFAULT 'test_description',
  original      VARCHAR(255) UNIQUE
);

COMMIT;
