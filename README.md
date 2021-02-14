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
Docker Image of this application has been already prepared. If you felt annoying when you use this application via our server, installing this image and create the container based on it and executing analysis is better for you.

Docker installation : https://docs.docker.com/engine/installation/

1. `docker pull takahiroyamada/gsoc_sbmlanalysis`
1. `docker run -it --rm -p 80:8080 takahiroyamada/gsoc_sbmlanalysis`

Then you can execute analysis using your favorite browser with the URL of "http://localhost/GSOC_WebMavenProject-0.0.1-SNAPSHOT/"
