# CentOS configuration
FROM tomcat:7.0-jre8
MAINTAINER takahiroyamada
# Java8 configuration 
ENV JAVA_HOME /docker-java-home/
ENV PATH $PATH:$JAVA_HOME/bin
ENV CLASSPATH $JAVA_HOME/jre/lib:$JAVA_HOME/lib:$JAVA_HOME/lib/tools.jar
ENV WEBAPP_PATH $CATALINA_HOME/webapps/GSOC_WebMavenProject-0.0.1-SNAPSHOT
# Tomcat Emvironment configuration
RUN echo CATALINA_OPTS=\"-Djava.library.path=$WEBAPP_PATH/WEB-INF/lib\" > $CATALINA_HOME/bin/setenv.sh
# Webapp deploy
ADD ./GSOC_WebMavenProject-0.0.1-SNAPSHOT.war ./webapps/GSOC_WebMavenProject-0.0.1-SNAPSHOT.war

# Other configuration
EXPOSE 8080
CMD ["catalina.sh", "run"]