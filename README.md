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

## Requirements
This project is created as Maven project. However the application of apache tomcat is not contatined to configure.
In development environment, Apache tomcat version 6.0.48 is used and it is stably worked.
Apache tomcat version 6.0.48 can be installed from following URL.

https://archive.apache.org/dist/tomcat/tomcat-6/v6.0.48/bin/
