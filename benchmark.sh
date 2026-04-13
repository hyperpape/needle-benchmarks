#!/usr/bin/bash
set -euo pipefail

# git version
version=$1
length=$2
benchmark=$3
timestamp=`date +%s`
filename=output/$benchmark$timestamp
PROF=""

echo `date` running benchmark $length $benchmark

if (( $# >= 4 )); then
    PROF="-prof $4"
else
    PROF=""
fi

if [[ $length == "short" ]];
then
   timing="-wi 1 -i 1 -r 10 -f 1"
elif [[ $length == "medium" ]];
then
    timing="-wi 3 -i 3 -r 10 -f 1"
elif [[ $length == "long" ]];
then
    timing="-wi 5 -i 5 -r 60 -f 1"
fi

echo "# $version$" > $filename

if [[ $benchmark == "all" ]];
then
    echo running "java -jar target/benchmarks.jar rf json $timing $PROF >> $filename 2>&1"
    java -jar target/benchmarks.jar -rf json $timing $PROF >> $filename 2>&1
else
    echo running "java -jar target/benchmarks.jar rf json $benchmark $timing $PROF >> $filename 2>&1"
    java -jar target/benchmarks.jar -rf json $benchmark $timing $PROF >> $filename 2>&1
fi

echo `date` finished running benchmark $length benchmark $timestamp
