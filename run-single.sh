#!/bin/sh

java  -Xmx256m -jar target/microbenchmarks.jar $*
# java  -Xmx256m -XX:+UnlockDiagnosticVMOptions -XX:+DebugNonSafepoints -jar target/microbenchmarks.jar $*

# TODO:
# java  -Xmx256m -XX:+DebugNonSafepoints -XX:+UnlockDiagnosticVMOptions \
#    -jar target/microbenchmarks.jar
#    -prof "async:output=flamegraph;dir=/tmp;libPath=/usr/local/async-profiler/curr/build/libasyncProfiler.so"
#	$*
