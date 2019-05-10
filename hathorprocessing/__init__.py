import os
import pandas
from sqlalchemy import create_engine


def read_fastq_data(chunksize=1000):
    conn = create_engine(os.getenv('DB_URL')).connect()
    sql = "SELECT * FROM fastq WHERE node_id='%s'" % (os.getenv('DB_NODE_ID'))
    return pandas.read_sql_query(sql, conn, chunksize=chunksize)


def read_prev_result():
    file = os.path.join(os.getenv('RESULT_PATH'), 'result.json')
    return pandas.read_json('file://' + file, orient='records')


def save_result(result):
    if not isinstance(result, pandas.DataFrame):
        raise ValueError('Result should be type of DataFrame')
    file = os.path.join(os.getenv('RESULT_PATH'), 'result.json')
    result.to_json(file, orient='records')
