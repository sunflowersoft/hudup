cd ../..
if [ "$1" != "" ]
then
	./build.sh -Dinclude-runtime-lib=$1 jar-server
else
	./build.sh jar-server
fi
cd tools/build-tools
