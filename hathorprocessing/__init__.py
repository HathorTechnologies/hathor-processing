import os
import pandas
from sqlalchemy import create_engine
import psycopg2

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
    connection = psycopg2.connect(dbname='database', password='secret', host=os.getenv('DB_URL'))
    cursor = connection.cursor()
    cursor.execute("SELECT * FROM fastq WHERE node_id=%s" % (os.getenv('DB_NODE_ID')))
    records = cursor.fetchcall()
    cursor.close()
    connection.close()
    # return pandas.read_sql_table('fastq', conn, columns=columns, chunksize=chunksize)


def read_prev_result():
    file = os.path.join(os.getenv('RESULT_PATH'), 'result.json')
    return pandas.read_json('file://' + file, orient='records')


def save_result(result):
    if not isinstance(result, pandas.DataFrame):
        raise ValueError('Result should be type of DataFrame')
    file = os.path.join(os.getenv('RESULT_PATH'), 'result.json')
    result.to_json(file, orient='records')
