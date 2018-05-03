cd ../..
if [ "$1" != "" ]
then
	./build.sh -Dinclude-runtime-lib=$1 jar-core
else
	./build.sh jar-core
fi
cd tools/build-tools
