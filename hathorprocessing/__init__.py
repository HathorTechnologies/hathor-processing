import os
import pandas
from sqlalchemy import create_engine

columns = (
    'instrument_name',
    'run_id',
    'flowcell_id',
    'flowcell_lane',
    'tile_number',
    'x_coord',
    'y_coord',
    'member',
    'is_filtered',
    'control_bit',
    'barcode',
    'data',
    'quality',
    'other'
)


def read_dna_data(chunksize=1000):
    conn = create_engine(os.getenv('DB_URL')).connect()
    return pandas.read_sql_table('dna', conn, columns=columns, chunksize=chunksize)


def read_prev_result():
    file = os.path.join(os.getenv('RESULT_PATH'), 'result.json')
    return pandas.read_json('file://' + file, orient='records')


def save_result(result):
    if not isinstance(result, pandas.DataFrame):
        raise ValueError('Result should be type of DataFrame')
    file = os.path.join(os.getenv('RESULT_PATH'), 'result.json')
    result.to_json(file, orient='records')
