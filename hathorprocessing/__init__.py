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


def read_fastq_data(chunksize=1000):
    conn = create_engine(os.getenv('DB_URL')).connect()
    return pandas.read_sql_table('fastq', conn, columns=columns, chunksize=chunksize)


def read_prev_result():
    file = os.path.join(os.getenv('RESULT_PATH'), os.getenv('TASK_ID'), 'result.json')
    return pandas.read_json('file://' + file, orient='records')


def save_result(result):
    if not isinstance(result, pandas.DataFrame):
        raise ValueError('Result should be type of DataFrame')

    path = os.path.join(os.getenv('RESULT_PATH'), os.getenv('TASK_ID'))
    if not os.path.exists(path):
        os.makedirs(path, exist_ok=True)
    result.to_json(path + '/result.json', orient='records')
