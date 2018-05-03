cd ../..
if [ "$1" != "" ]
then
	./build.sh -Dinclude-runtime-lib=$1 jar-listener
else
	./build.sh jar-listener
fi
cd tools/build-tools
