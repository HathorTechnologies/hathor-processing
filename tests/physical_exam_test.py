import os
import pandas
import unittest
from unittest.mock import patch
from hathorprocessing import physical_exam
from hathorprocessing import save_result, read_prev_result


class PhysicalExam(unittest.TestCase):

    def test_read(self):
        env = patch.dict('os.environ', {
            'DB_URL': 'sqlite:////' +
                      os.path.join(os.getcwd(), 'resources', 'hathor_node.db'),
            'RESULT_PATH': os.path.join(os.getcwd(), 'results')
        })

        env.start()
        result = physical_exam.read_data(10)
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
        for data in physical_exam.read_data(10):
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
