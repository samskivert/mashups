#!/bin/sh

if [ -z "$1" -o -z "$2" ]; then
    echo "Usage: $0 artifactId JavaGameClassName"
    exit 255
fi

GROUPID=com.samskivert.mashups
VERS=2.0-SNAPSHOT

mvn archetype:generate \
    -DarchetypeRepository=local \
    -DarchetypeRepository=$HOME/.m2/repository \
    -DarchetypeGroupId=io.playn \
    -DarchetypeArtifactId=playn-archetype \
    -DarchetypeVersion=$VERS \
    -DgroupId=$GROUPID \
    -DartifactId=$1 \
    -Dversion=1.0-SNAPSHOT \
    -DJavaGameClassName=$2 \
    -Dpackage=$1
