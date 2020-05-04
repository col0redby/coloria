-- Deploy coloria:table_image_versions to mysql
-- requires: database_coloria
-- requires: table_images

BEGIN;

-- XXX Add DDLs here.

USE coloria;

CREATE TABLE IF NOT EXISTS image_versions
(
  id                       MEDIUMINT AUTO_INCREMENT PRIMARY KEY,
  image_id                 MEDIUMINT,
  version                  VARCHAR(100),
  processing_time_millis   INT,
  UNIQUE (image_id,version),
  FOREIGN KEY (image_id) REFERENCES images (id) ON DELETE CASCADE
);

COMMIT;
