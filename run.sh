#!/usr/bin/env bash
if [ ! -d ./result ]; then
    mkdir ./result
    chmod -R 777 ./result
    setfacl -d -m o::rwx ./result
fi

docker run -it --rm --name my-running-script \
-v $PWD/tmp:/usr/src/app \
-v $PWD/result:/result \
-v $PWD/db:/db \
-e RESULT_PATH=/result \
-e TASK_ID=00000000-0000-0000-0000-000000000001 \
-e DB_URL=sqlite:////db/hathor_node.db \
hathortechnologies/processing:dev \
python test.py
