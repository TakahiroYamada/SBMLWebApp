# SBMLWebApp
## Overview
### Introduction
In systems biology, it is very important to standardize the model for a given biological phenomenon
and analyze the system based on this model. Such a model is most of the time based on ordinary differential equation (ODE).

Such a systems biology  model is normally described in a standard format, with [Systems Biology Markup Language (SBML)](http://sbml.org)
being the de-facto standard in the field. Several software tools exist to create SBML models easily.
Regarding the analysis of such models, which is mainly via simulation, steady state analysis, or parameter estimation.

* `Time course simulation` provides the information of each species in the model over time which enables us to
understand the dynamical variation of species in model.

* `Steady state analysis` can give us the values of model objects after enough time has passed and a steady state is reached.

* `Parameter estimation` focuses on the identification of unknown parameters
in the model based on experimental data.

Time course simulation, steady state simulation and parameter estimation are implemented in multiple
libraries and tools for SBML, e.g. COPASI, libroadrunner, or the Simulation Core Library. However, establishing the environment for such analysis
is often difficult for biologists and a major obstacle to analyze ODE models easily.
### Project goals

![GSOC 2017](./docs/images/gsoc-icon.png)
This project is part of [Google Summer of Code 2017](https://summerofcode.withgoogle.com/) with the goal of
developing a Web App for SBML models which provides functionality for

* `time course simulation`
* `steady state analysis`
* `parameter estimation`

The Web App will provide options for uploading SBML models and experimental data files and run the respective
simulations with the models.

### Repository Content
This repository includes the following files and directories

* `docs/` SBML example files for analysis
* `examples/` SBML example files for analysis
* `lib/` Library dependencies
* `src/` Source code for web app
* `WebContent/` Web content for web app
* `README.md` Overview information
* `pom.xml` Maven pom file

### Additional information
GitHub: https://github.com/TakahiroYamada/SBMLWebApp  
Blog: http://gsoc2017developwebservice.blogspot.jp  
PivotalTracker: https://www.pivotaltracker.com/n/projects/2020229

## Installation
This project is created in Java as a Maven project. To run the web app locally one can
run the server from within eclipse.

### Tomcat download
Apache tomcat is not contained in the installation, but must be installed separately. During the development
`Apache TomCat v6.0.48` is used which is available as `apache-tomcat-6.0.48.tar.gz` from
`https://archive.apache.org/dist/tomcat/tomcat-6/v6.0.48/bin/`

After download the files should be unpacked in the correct position (e.g. :`/Applications/`).
### Eclipse
#### Eclipse tomcat plugin installation
When you use Eclipse , eclipse tomcat plugin is needed. This file can be downloaded following Task.
1. Select Help - Eclipse Marketplace
1. Type "tomcat" in find text box
1. Install "Eclipse Tomcat Plugin 9.1.2"

Then you start-up eclipse. When you can see the icon of tomcat in Eclipse , the install is accomplished.

#### Tomcat installation in Eclipse
After you complete installation of eclipse tomcat plugin, you should choose the `tomcat version`
and `tomcat home` from `Environment -> Tomcat`. For version, please check version 6.x and select
the aforementioned distribution of `apache-tomcat-6.0.48`.

#### Run Server
After finishing the tomcat configuration one can run the server in Eclipse via:

1. Clone repository `git clone https://github.com/TakahiroYamada/SBMLWebApp.git`
1. Select `File -> Open Projects from File System`
1. Choose the directory of this project in `Import source`
1. Select `Properties -> Java Build Path -> Libraries - add library - Server Runtime - Apache Tomcat v6.0`

### Jetbrains idea
* `Run | Edit Configurations | + | Tomcat Server | Local`
* Select tomcat: `Configure -> Select apache-tomcat-6.0.48`
* Select artifact to deploy: `Artifact GSOC_WebMavenProject:war`
* Add external libraries to `java.library.path` in VM options i.e.
```
-Djava.library.path=/path/to/SBMLWebApp/lib/COPASI-4.19.140-Java-Bindings-Darwin
```

see also https://www.jetbrains.com/help/idea/2017.1/run-debug-configuration-tomcat-server.html

### Docker Image
Docker composed container of this application has been already prepared. If you felt annoying when you use this application via our server, running composed containers and executing analysis is better for you.

Docker installation : https://docs.docker.com/engine/installation/

Docker-compose installation : https://docs.docker.com/compose/install/

We have confirmed that the docker image works in the following environment.

  . | OS version | docker | docker-compose 
-- | -- | -- | --
macOS | macOS Mojave 10.14.6 x86_64 | 20.10.7 | 1.29.2
Linux | Ubuntu 18.04.6 LTS x86_64 | 20.10.8 | 1.17.1

1. for macOS
  ```sh
  $ cd docker
  $ export LOCAL_HOST_IP=$(/sbin/ifconfig en0 | awk '/inet / { print $2 }') # or export LOCAL_HOST_IP=(Your Private IP Address)
  $ docker-compose up -d
  ```

2. for Linux
  ```sh
  $ cd docker
  $ export DEV_ETHER=$(/sbin/route | grep default | awk '{print $8}')
  $ export LOCAL_HOST_IP=$(/sbin/ifconfig $DEV_ETHER | awk '/inet / { print $2 }') # or export LOCAL_HOST_IP=(Your Private IP Address)
  $ unset DEV_ETHER
  $ sudo LOCAL_HOST_IP=$LOCAL_HOST_IP docker-compose up -d
  ```

Then you can execute analysis using your favorite browser with the URL of `http://localhost/SBMLWebApp/`

When you want to finish it, please type `docker-compose down`.

#### Building the project via Maven

After checking out the project using `git clone` make sure the folder `WebContent/` contains required third-party applications.
In some cases, recursive cloning might not work properly.

The followoing steps are only necesary if you cannot find the subfolders `CytoScape`, `ScrollTrigger`, and `FileSaver.js` within the folder `WebContent/` or if they are empty, please navigate to `WebContent/` and use `git clone` for these three:

1. `git clone https://github.com/terwanerik/ScrollTrigger.git`
2. `git clone https://github.com/eligrey/FileSaver.js.git` and
3. `git clone https://github.com/cytoscape/cytoscape.js.git`

Next, please rename the folder `cytoscape.js` to `CytoScape` and make sure that the right versions are present by checking out the following specific git tags within the correspoinding sub-repositories:

* FileSaver.js : v2.0.4 → move to the folder and use `git checkout tags/v2.0.4`
* cytoscape.js : v3.2.3 → move to the folder and use `git checkout tags/v3.2.3`
* ScrollTrigger : v0.3.6 → move to the folder and use `git checkout tags/v0.3.6`

Now, you should navigate back to the project's root folder where you can run
```
mvn package
```
to build the project.

The main problems that might prevent it from succesfully compiling the project could be missing links to required dependencies.
Your system might not find the [Systems Biology Simulation Core Library (SBSCL)](https://github.com/draeger-lab/SBSCL) version 1.5, libSBML, or libSBMLsim.
For the libSBML dependency, it is, on most systems, sufficient to set the environment variable `LD_LIBRARY_PATH` to the directory where libSBML is installed on your system.
LibSBMLsim and SBSCL can be copied over to the subfolder `lib` if missing:
* `lib/SimulationCore/dist/SimulationCoreLibrary-1.5/SimulationCoreLibrary_v1.5_slim.jar`
* `lib/libSBMLsim/build/src/bindings/java/libsbmlsimj.jar`.
You can download SBSCL version 1.5 from the [corresponding project](https://github.com/draeger-lab/SBSCL/tree/v1.5.0) and name it `SimulationCoreLibrary_v1.5_slim.jar` after placing it at the specified location.
On many systems you may need to build libSBMLsim yourself and place it in the right folder (see the [build instructions](https://github.com/libsbmlsim/libsbmlsim)).
Before proceeding, you may want to run `mvn clean` before attempting another build process with `mvn package`.

If everything worked as supposed, you should now find the `war` file in the subfolder `target` of the project's root.
The next steps are:
1. Distributing created `war` file under target directory to the apache tomcat `webapp` directory
2. Running RabbitMQ with `rabbitmq-server` after the configuration written before.
3. Running TomCat with executing `startup.sh` under `bin` directory of apache tomcat.
