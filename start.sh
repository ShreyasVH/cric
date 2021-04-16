#!/usr/bin/env bash
sbt -jvm-debug "8059" -Dlogger.file=conf/logger.xml -Dhttps.port="10059" "run 9059";