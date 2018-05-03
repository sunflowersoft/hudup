cd ../..
if [ "$1" != "" ]
then
	./build.sh -Dinclude-runtime-lib=$1 jar-toolkit
else
	./build.sh jar-toolkit
fi
cd tools/build-tools
