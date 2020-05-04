-- Deploy coloria:table_version_colors to mysql
-- requires: database_coloria
-- requires: table_image_versions

BEGIN;

-- XXX Add DDLs here.

USE coloria;

CREATE TABLE IF NOT EXISTS version_colors
(
  id                 MEDIUMINT AUTO_INCREMENT PRIMARY KEY,
  image_version_id   MEDIUMINT,
  color              CHAR(7),
  pct                FLOAT NOT NULL,
  FOREIGN KEY (image_version_id) REFERENCES image_versions (id) ON DELETE CASCADE
);

COMMIT;
