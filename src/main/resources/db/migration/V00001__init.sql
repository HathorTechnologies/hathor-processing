CREATE TABLE files (
  id SERIAL PRIMARY KEY,
  data_id UUID,
  file_id UUID NOT NULL,
  original_name VARCHAR NOT NULL,
  size_kb NUMERIC(12,3) NOT NULL,
  mime_type VARCHAR(50) NOT NULL,
  updated_time TIMESTAMP WITHOUT TIME ZONE DEFAULT now() NOT NULL,
  created_time TIMESTAMP WITHOUT TIME ZONE DEFAULT now() NOT NULL
);

CREATE UNIQUE INDEX table_name_file_id_uindex ON files(file_id);