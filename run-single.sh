#!/bin/sh

java  -Xmx256m -jar target/microbenchmarks.jar -XX:+DebugNonSafepoints $*

# java  -Xmx256m -jar target/microbenchmarks.jar -XX:+DebugNonSafepoints -XX:+UnlockDiagnosticVMOptions $*

# TODO:
# java  -Xmx256m -jar target/microbenchmarks.jar -XX:+DebugNonSafepoints -XX:+UnlockDiagnosticVMOptions \
# -prof "async:output=flamegraph;dir=/tmp;libPath=/usr/local/async-profiler/curr/build/libasyncProfiler.so"
#	$*
