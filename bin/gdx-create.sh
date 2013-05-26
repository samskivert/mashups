#!/bin/sh
#
# A script for creating a new mashup entry

if [ -z "$1" -o -z "$2" ]; then
    echo "Usage: $0 artifactId JavaGameClassName"
    exit 255
fi

VERS=0.9.9-SNAPSHOT

mvn archetype:generate \
    -DarchetypeRepository=local \
    -DarchetypeRepository=$HOME/.m2/repository \
    -DarchetypeGroupId=com.badlogic.gdx \
    -DarchetypeArtifactId=gdx-archetype \
    -DarchetypeVersion=$VERS \
    -DgroupId=com.samskivert.mashups \
    -DartifactId=$1 \
    -Dversion=1.0-SNAPSHOT \
    -DJavaGameClassName=$2 \
    -Dpackage=$1
