# GSOC2017 Web App for SBML Model Analysis
## Introduction
In systems biology, it is very important to standardize the model for a given biological phenomenon 
and analyze the system based on this model. Such a model is most of the time based on ordinary differential equation (ODE).
 
Such a systems biology  model is normally described in a standard format, with Systems Biology Markup Language (SBML) 
being the de-facto standard in the field. Several software tools exist to create SBML models easily. 
Regarding the analysis of such models, which is mainly via simulation, steady state analysis, or parameter estimation. 

* `Time course simulation` provides the information of each species in the model over time which enables us to 
understand the dynamical variation of species in model. 

* `Steady state analysis` can give us the values of model objects after enough time has passed and a steady state is reached. 
 
* `Parameter estimation` focuses on the identification of unknown parameters 
in the model based on experimental data. 

Time course simulation, steady state simulation and parameter estimation are implemented in multiple 
libraries and tools for SBML, e.g. COPASI or libroadrunner. However, establishing the environment for such analysis 
is often difficult for biologists and a major obstacle to analyze ODE models easily.

This project aims at developing a Web App for SBML models which provides functionality for

* `time course simulation`
* `steady state analysis`
* `parameter estimation`

The Web App will provide options for uploading SBML models and experimental data files and run the respective 
simulations with the models.

## Additional information
GitHub: https://github.com/TakahiroYamada/GSOC2017_SBMLModelAnalysisWebApp  
Blog: http://gsoc2017developwebservice.blogspot.jp  
PivotalTracker: https://www.pivotaltracker.com/n/projects/2020229  

## Repository Content
This repository includes the following files and directories 

* `examples/` SBML example files for analysis
* `lib/` Library dependencies
* `src/` Source code for web app
* `WebContent/` Web content for web app
* `README.md` Overview information
* `pom.xml` Maven pom file

## Install Tomcat
The web app is implemented in Java using a tomcat installation. 
To run the web app locally one can run the server from within eclipse.

### Tomcat download
This project is created as Maven project. However the application of apache tomcat is not contatined to configure.
In development environment, Apache tomcat version 6.0.48 is used and it is stably worked.
Apache tomcat version 6.0.48 can be installed from following URL. The file name is apache-tomcat-6.0.48.tar.gz. When you download this file, the direcotry should be distributed in your proper position(ex:/Applications/). 
Tomcat : https://archive.apache.org/dist/tomcat/tomcat-6/v6.0.48/bin/

### Eclipse tomcat plugin installation
When you use Eclipse , eclipse tomcat plugin is needed. This file can be downloaded following Task.
1. Select Help - Eclipse Marketplace
1. Type "tomcat" in find text box
1. Install "Eclipse Tomcat Plugin 9.1.2"

Then you start-up eclipse. When you can see the icon of tomcat in Eclipse , the install is accomplished.

### Tomcat installation in Eclipse
After you complete installation of eclipse tomcat plugin, you should choose the `tomcat version` 
and `tomcat home` from `Environment -> Tomcat`. For version, please check version 6.x and select 
the aforementioned distribution of `apache-tomcat-6.0.48`.

## Run Tomcat server
After finishing the tomcat configuration one can run the server in Eclipse via:

1. Type `git clone https://github.com/TakahiroYamada/GSOC2017_SBMLModelAnalysisWebApp.git`
1. Select `File -> Open Projects from File System`
1. Choose the directory of this project in `Import source`
1. Select `Properties -> Java Build Path -> Libraries - add library - Server Runtime - Apache Tomcat v6.0`
