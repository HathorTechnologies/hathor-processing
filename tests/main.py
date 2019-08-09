import sys
sys.path.append("..")
import unittest

testmodules = [
    'allergies_test',
    'apache_aps_test',
    'apache_patient_result_test',
    'apache_pred_var_test',
    'care_plan_care_provider_test',
    'care_plan_eol_test',
    'care_plan_general_test',
    'care_plan_goal_test',
    'care_plan_infectious_disease_test',
    'custom_lab_test',
    'diagnosis_test',
    'dna_test',
    'drugs_test',
    'dx_test',
    'hospital_test',
    'infusion_drug_test',
    'intake_output_test',
    'lab_test',
    'medication_test',
    'micro_lab_test',
    'note_test',
    'nurse_assessment_test',
    'nurse_care_test',
    'nurse_charting_test',
    'past_history_test',
    'patient_test',
    'physical_exam_test',
    'respiratory_care_test',
    'respiratory_charting_test',
    'treatment_test',
    'vital_aperiodic_test',
    'vital_periodic_test',
]

suite = unittest.TestSuite()

for t in testmodules:
    try:
        # If the module defines a suite() function, call it to get the suite.
        mod = __import__(t, globals(), locals(), ['suite'])
        suitefn = getattr(mod, 'suite')
        suite.addTest(suitefn())
    except (ImportError, AttributeError):
        # else, just load all the test cases from the module.
        suite.addTest(unittest.defaultTestLoader.loadTestsFromName(t))

unittest.TextTestRunner().run(suite)
