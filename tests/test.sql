CREATE TABLE fastq
(
    id              UUID PRIMARY KEY NOT NULL,
    instrument_name VARCHAR(20)      NOT NULL,
    run_id          REAL,
    flowcell_id     VARCHAR(50),
    flowcell_lane   INT,
    tile_number     INT,
    x_coord         INT,
    y_coord         INT,
    member          INT,
    is_filtered     BOOL,
    control_bit     INT,
    barcode         VARCHAR(50),
    data            VARCHAR,
    quality         VARCHAR,
    other           VARCHAR,
    node_id         UUID             NOT NULL
);