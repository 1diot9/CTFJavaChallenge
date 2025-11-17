#!/bin/bash

echo $FLAG > /flag
unset FLAG

chmod 400 /flag
chmod 4755 /readflag

su -p ctf -c "java -jar /app/EasyDB.jar"
