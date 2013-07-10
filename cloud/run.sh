#! /usr/bin/env sh

export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-i386
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=$JAVA_HOME/lib/dt.jar:.:$JAVA_HOME/lib/tools.jar:$JAVA_HOME/jre/lib/rt.jar

cd /home/sjucker/sicsthsense/cloud && /home/sjucker/playframework/play-2.1.2/play "$@"
