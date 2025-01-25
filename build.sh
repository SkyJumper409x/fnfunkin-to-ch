#!/bin/bash

if [ "$1" = "--clean-before-build" ]; then rm -r ./target/classes/*; fi;
cd src
find -name "*.java" > sources.txt
javac -d ../target/classes -cp ../lib/*.jar @sources.txt
rm sources.txt
cd ..
