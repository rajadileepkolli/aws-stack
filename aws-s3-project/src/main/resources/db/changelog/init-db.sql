CREATE SEQUENCE sequence_generator
  START WITH 1
  INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS file_info (
    id SERIAL PRIMARY KEY,
    file_name varchar(64) NOT NULL,
    file_url varchar(328) NOT NULL,
    is_upload_success_full boolean
);