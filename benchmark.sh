/usr/java/jdk-11/bin/java -jar target/benchmarks.jar -rf json $@ -wi 5 -i 5 -r 60 -f 1 $1  > output/$1$(date +%s) 2>&1
