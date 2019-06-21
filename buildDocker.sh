#!/usr/bin/env bash

docker build . --tag hathortechnologies/processing:1.2.0.dev --tag hathortechnologies/processing:dev
docker push hathortechnologies/processing:1.2.0.dev
docker push hathortechnologies/processing:dev
