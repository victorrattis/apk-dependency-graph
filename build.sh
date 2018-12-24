cd apk-dependecy-graph-lib
ant
cd ..

LIB_FILE=apk-dependecy-graph-lib/build/jar/apk-dependency-graph.jar
if [ -f "$LIB_FILE" ]; then
    mv "$LIB_FILE" gui/lib/
    echo "done"
else 
    echo "apk-dependency-graph.jar does not exist!"
fi