import hathorprocessing

table = 'apache_pred_var'

columns = (
    "apachepredvarid",
    "patientunitstayid",
    "sicuday",
    "saps3day1",
    "saps3today",
    "saps3yesterday",
    "gender",
    "teachtype",
    "region",
    "bedcount",
    "admitsource",
    "graftcount",
    "meds",
    "verbal",
    "motor",
    "eyes",
    "age",
    "admitdiagnosis",
    "thrombolytics",
    "diedinhospital",
    "aids",
    "hepaticfailure",
    "lymphoma",
    "metastaticcancer",
    "leukemia",
    "immunosuppression",
    "cirrhosis",
    "electivesurgery",
    "activetx",
    "readmit",
    "ima",
    "midur",
    "ventday1",
    "oobventday1",
    "oobintubday1",
    "diabetes",
    "managementsystem",
    "var03hspxlos",
    "pao2",
    "fio2",
    "ejectfx",
    "creatinine",
    "dischargelocation",
    "visitnumber",
    "amilocation",
    "day1meds",
    "day1verbal",
    "day1motor",
    "day1eyes",
    "day1pao2",
    "day1fio2"
)


def read_data(chunksize=1000):
    return hathorprocessing.read_data(table, columns, chunksize)