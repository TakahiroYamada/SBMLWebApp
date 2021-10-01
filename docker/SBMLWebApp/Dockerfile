# TomCat7.0 configuration
FROM tomcat:7.0-jre8
MAINTAINER takahiroyamada
# Java8 configuration 
ENV JAVA_HOME /docker-java-home/
ENV PATH $PATH:$JAVA_HOME/bin
ENV CLASSPATH $JAVA_HOME/jre/lib:$JAVA_HOME/lib:$JAVA_HOME/lib/tools.jar
ENV WEBAPP_PATH $CATALINA_HOME/webapps/SBMLWebApp

# libSBML configuration
RUN apt-get -y update && apt-get install -y cmake gcc g++ libxml2-dev
RUN wget --no-check-certificate https://sourceforge.net/projects/sbml/files/libsbml/5.13.0/experimental/source/libSBML-5.13.0-Source.tar.gz/download -O /tmp/libsbml.tgz -q && \
      cd /tmp && \
      tar xzf libsbml.tgz && \
      mkdir /tmp/libSBML-5.13.0-Source/build && \
      cd /tmp/libSBML-5.13.0-Source/build && \
      cmake .. -DCMAKE_INSTALL_PREFIX="/usr" -DWITH_CPP_NAMESPACE="ON" -DENABLE_ARRAYS="ON" -DENABLE_COMP="ON" -DENABLE_DISTRIB="ON" -DENABLE_DYN="ON" -DENABLE_FBC="ON" -DENABLE_GROUPS="ON" -DENABLE_LAYOUT="ON" -DENABLE_MULTI="ON" -DENABLE_QUAL="ON" -DENABLE_RENDER="ON" -DENABLE_REQUIREDELEMENTS="ON" -DENABLE_SPATIAL="ON" && \
      make install && \
      rm /tmp/libsbml.tgz

# Tomcat Environment configuration
RUN echo CATALINA_OPTS=\"-Djava.library.path=$WEBAPP_PATH/WEB-INF/lib\" > $CATALINA_HOME/bin/setenv.sh
# Webapp deploy
ADD ./SBMLWebApp.war ./webapps/SBMLWebApp.war

# Other configuration
EXPOSE 8080
CMD ["catalina.sh", "run"]
