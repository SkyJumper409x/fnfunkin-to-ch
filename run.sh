#!/bin/bash

# cd target/classes
# ./..
# \.\./\.
java -ea -cp "$(echo ./lib/*.jar | sed 's^\.jar \./lib^.jar:./lib^g'):./target/classes/" xyz.skyjumper409.fnftoch.MyMain "$@"
# cd ../..
