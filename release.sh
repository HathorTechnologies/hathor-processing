#!/usr/bin/env bash
git checkout master
git pull
bumpversion patch
python setup.py sdist bdist_wheel upload
bumpversion --no-tag patch
git push origin master --tags
