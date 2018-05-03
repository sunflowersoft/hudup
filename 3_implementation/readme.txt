Hudup product is the recommender framework dedicated to scientists and software developers who create or deploy recommendation solutions and algorithms in e-commerce and e-learning.

Hudup application has five main modules such as Evaluator, Server, Listener, Balancer and one additional module Toolkit. Note that the Server, Listener and Balancer service ports are 10151, 10152 and 10154, respectively. Toolkit module is used to configure, create and modify datasets.

There are 4 Hudup installer packages bundled with Java runtime:
- Package "Hudup-${version}-windows-jre1.7.exe" is Windows 32 bit installer. This is the most common one.
- Package "Hudup-${version}-windows-x64-jre1.7.exe" is Windows 64 bit installer.
- Package "Hudup-${version}-unix-jre1.7.sh" is Unix/Linux 32 bit installer.
- Package "Hudup-${version}-macos-jre1.7.dmg" is Mac OS installer download JRE automatically.

The current version is 11, which supports Java 1.8.0_101-b13 and Java compiler (javac) 1.8.0_60.

After installing Hudup in your computer by executing appropriate installer package, you start Hudup application and there are three experimental scripts:

1. Running access point Evaluator. You can choose "Batch evaluate" tab, press "Load script" button and open "batch-sample.script" file at current directory. All sample datasets in folder "datasets" at current directory.

2. Running access point Server, which in turn, running access point Evaluator so as to deploy Hudup in client-server environment. Server automatically remind you to set up Server configuration, for default, you only press "Next" button and finish set up. Note that you must select "rmi_server_query" algorithm in Evaluator in order to evaluate Hudup in client-server environment.

3. Running access point Server, which in turn, running access point Listener or Balancer, which in turn, running access point Evaluator so as to deploy Hudup in client-listener-server environment. You must select "socket_server_query" algorithm in Evaluator in order to evaluate Hudup in client-listener-server environment. 


Hudup software also provides 10 developing jar packages:
- datasets.zip: contains movielens datasets
- hudup-${version}.zip: contains hudup.jar including five modules Evaluator, Server, Listener, Balancer and Toolkit.
- hudup-${version}-all-in-one.zip: contains whole Hudup product both code and source.
- hudup-${version}-core.zip: contains core classes
- hudup-${version}-doc.zip: contains Hudup document and API
- hudup-${version}-evaluator.zip: contains hudup-evaluator.jar which is Evaluator module
- hudup-${version}-listener.zip: contains hudup-listener.jar which is Listener module
- hudup-${version}-server.zip: contains hudup-server.jar which is Server module
- hudup-${version}-toolkit.zip: contains hudup-toolkit.jar which is Toolkit module
- runtime-lib.jar: contains libraries Hudup framework use

When you uncompress hudup-${version}-*.zip, there are a respective hudup-*.jar module and shell command files used to execute such jar module. You should copy runtime-lib.jar to the same directory with such jar module for running successfully. You can copy these jar modules to your application's library directory in order to develop new software.

Thank you for enjoying product
Best regards
