import os
import pandas
import unittest
from sqlalchemy import create_engine
from unittest.mock import patch

from testcontainers.postgres import PostgresContainer

from hathorprocessing import read_fastq_data, save_result, read_prev_result


class TestCommons(unittest.TestCase):

    conn_url = ''

    @classmethod
    def setUpClass(cls):
        file = open('test.sql', 'r')
        sql = " ".join(file.readlines())
        postgres = PostgresContainer("postgres:9.5")
        conn_url = postgres.get_connection_url()
        engine = create_engine(postgres.get_connection_url())
        conn = engine.connect()
        engine.execute(sql)
        conn.close()
        file.close()

    def test_read_fastq(self):
        env = patch.dict('os.environ', {
            'DB_URL': 'sqlite:////' +
                      os.path.join(os.getcwd(), 'resources', 'hathor_node.db'),
            'RESULT_PATH': os.path.join(os.getcwd(), 'results')
        })

        env.start()
        result = read_fastq_data(10)
        self.assertIsNotNone(result)
        for data in result:
            self.assertEqual(len(data), 10)
        env.stop()

    def test_save_result(self):
        env = patch.dict('os.environ', {
            'DB_URL': 'sqlite:////' +
                      os.path.join(os.getcwd(), 'resources', 'hathor_node.db'),
            'RESULT_PATH': os.path.join(os.getcwd(), 'results')
        })

        env.start()
        result = []
        for data in read_fastq_data(10):
            result.append(data)
        save_result(pandas.concat(result))
        result_file = os.path.join(os.getenv('RESULT_PATH'), 'result.json')
        self.assertTrue(os.path.exists(result_file))
        env.stop()

    def test_read_prev_result(self):
        env = patch.dict('os.environ', {
            'RESULT_PATH': os.path.join(os.getcwd(), 'prev_result')
        })

        env.start()
        result = read_prev_result()
        self.assertIsNotNone(result)
        self.assertEqual(len(result), 1)
        self.assertEqual(result['instrument_name'][0], 'SRR000063')
        self.assertEqual(result['run_id'][0], 18.3999996185)
        self.assertEqual(result['flowcell_id'][0], 'EJGTJSJ01AUQGP')
        self.assertEqual(result['data'][0], 'AATACCAGCCTGAGCGGGCTGGCAAGGCNNNN')
        self.assertEqual(result['quality'][0], '@:7;A<;9B<8;;8<CA2<1A=<>:A<<!!!!')
        env.stop()

    def test_postgres(self):
        env = patch.dict('os.environ', {
            'DB_URL': conn_url,
            'RESULT_PATH': os.path.join(os.getcwd(), 'results'),
            'DB_NODE_ID': '00000000-0000-0000-0000-000000000001'
        })
        env.start()
        result = read_fastq_data(10)
        self.assertIsNotNone(result)
        env.stop()
