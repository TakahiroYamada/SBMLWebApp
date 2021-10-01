# TomCat7.0 configuration
FROM tomcat:7.0-jre8
MAINTAINER takahiroyamada
# Java8 configuration 
ENV JAVA_HOME /docker-java-home/
ENV PATH $PATH:$JAVA_HOME/bin
ENV CLASSPATH $JAVA_HOME/jre/lib:$JAVA_HOME/lib:$JAVA_HOME/lib/tools.jar
ENV WEBAPP_PATH $CATALINA_HOME/webapps/SBMLWebApp
# Tomcat Environment configuration
RUN echo CATALINA_OPTS=\"-Djava.library.path=$WEBAPP_PATH/WEB-INF/lib\" > $CATALINA_HOME/bin/setenv.sh
# Webapp deploy
ADD ./SBMLWebApp.war ./webapps/SBMLWebApp.war

# Other configuration
EXPOSE 8080
CMD ["catalina.sh", "run"]
