# GSOC2017_SBMLModelAnalysisWebApp
## Introduction
In systems biology, it is very important to formalize the model for biological phenomenon and analyze based on this model basically constructed by ordinary differential equation (ODE). The model is normally described by Systems Biology Markup Language (SBML) and there are several software tools to create the model easily. Regarding the analysis, this is mainly considered as simulation, steady state analysis, and parameter estimation. Using method of simulation, we get the realization of each species in model by the minute. This result enables us to understand the dynamical variation of species in model. Steady state analysis can give us the final expression in time enough passed. This approach is effective to estimate whether each species is worked in considered condition. The parameter estimation focuses on the identification of unknown parameters in model considering the expression value obtained by experiment. These methodologies are mathematically formalized and the library to analyze, COPASI for instance, already exists. However, establishing the environment of analysis is difficult for biologists. Therefore, this is one of the obstacle to analyze ODE model easily.

The distribution includes the following files and directories 

README           this file

WebContent       HTML file in client side is included

lib              Dependent library

src              Source file in server side is included

target           Maven target file is included

pom.xml          Maven pom file

## Tomcat configuration
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
After you complete installation of eclipse tomcat plugin, you should choose version of tomcat and tomcat home from Environment - Tomcat. In version, please check version 6.x and select the aforementioned distribution of apache-tomcat-6.0.48.

The configuration is completed.

## Introduction of installation
When you finish tomcat configuration, you can set-up this software following tasks in Eclipse.

1. Type "git clone https://github.com/TakahiroYamada/GSOC2017_SBMLModelAnalysisWebApp.git"
1. Select File - Open Projects from File System...
1. Choose the directory of this project in "Import source"
1. Select Properties - Java Build Path - Libraries - add library - Server Runtime - Apache Tomcat v6.0
