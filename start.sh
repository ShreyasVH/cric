#!/usr/bin/env bash

set -o allexport
source .env;
set +o allexport

activator -jvm-debug "8059" -Dhttps.port="10059" "run 9059";