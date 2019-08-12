import codecs
import os
import re

from setuptools import setup

here = os.path.abspath(os.path.dirname(__file__))


def read(*parts):
    # intentionally *not* adding an encoding option to open, See:
    #   https://github.com/pypa/virtualenv/issues/201#issuecomment-3145690
    with codecs.open(os.path.join(here, *parts), 'r') as fp:
        return fp.read()


def find_version(*file_paths):
    version_file = read(*file_paths)
    version_match = re.search(
        r"^__version__ = ['\"]([^'\"]*)['\"]",
        version_file,
        re.M,
    )
    if version_match:
        return version_match.group(1)

    raise RuntimeError("Unable to find version string.")


setup(
    name='hathor-processing',
    version=find_version("hathorprocessing", "version.py"),
    packages=['hathorprocessing'],
    url='https://github.com/HathorTechnologies/hathor-processing',
    license='',
    author='',
    author_email='',
    description='',
    python_requires='>=3',
    install_requires=[
        'pandas',
        'sqlalchemy'
    ],
)
