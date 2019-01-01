cd apk-dependecy-analyzer
ant
cd ..

LIB_FILE=apk-dependecy-analyzer/build/jar/apk-dependency-graph.jar
if [ -f "$LIB_FILE" ]; then
    mv "$LIB_FILE" app/lib/ 
else 
    echo "apk-dependency-graph.jar not found!"
fi