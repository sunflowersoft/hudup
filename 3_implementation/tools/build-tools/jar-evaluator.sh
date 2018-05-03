cd ../..
if [ "$1" != "" ]
then
	./build.sh -Dinclude-runtime-lib=$1 jar-evaluator
else
	./build.sh jar-evaluator
fi
cd tools/build-tools
