#!/usr/bin/env bash

python setup.py sdist bdist_wheel
# need credentials -u -p
twine upload dist/*
