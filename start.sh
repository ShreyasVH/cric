#!/usr/bin/env bash
dos2unix .env;
export $(xargs < .env);

sbt -jvm-debug "8059" -Dlogger.file=conf/logger.xml -Dhttps.port="10059" "run 9059";