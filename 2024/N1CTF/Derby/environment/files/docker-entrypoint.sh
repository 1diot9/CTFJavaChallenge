#!/bin/bash

source /flag.sh

groupadd -r app
useradd -r -g app app

chmod 400 /flag
chmod 4755 /readflag

chown -R app:app /app

su app -c "cd /app; java -jar Derby.jar"