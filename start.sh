#!/usr/bin/env bash

#if [[ ! $(lsof -i:3306 -t | wc -l) -gt 0 ]]; then
#	echo "Starting mysql"
#	mysqld_safe --defaults-file=/home/shreyas/.asdf/installs/mysql/8.0.31/conf/my.cnf > mysql.log 2>&1 &
#fi

#while [[ ! $(lsof -i:3306 -t | wc -l) -gt 0 ]];
#do
#	echo "Waiting for mysql"
#done

#set -o allexport;
#source .env;
#set +o allexport;
sbt -jvm-debug "8059" -Dlogger.file=conf/logger.xml "run 9059";
