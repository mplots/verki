FROM java:openjdk-8-jdk

MAINTAINER maris.plots@gmail.com

ENV UID=30401
ENV REST_WORK=/opt/instagram-client
ENV JAVA_OPTS=-Xmx128M
ENV USER=instagram-client

RUN useradd -r -u ${UID} -m -c "iot rest role account" -d ${REST_WORK} -s /bin/false ${USER}

COPY maven/instagram-client-*.jar ${REST_WORK}/app.jar

RUN mkdir ${REST_WORK}/data &&  chown ${USER} -R ${REST_WORK}

USER ${USER}

EXPOSE 8080

CMD java ${JAVA_OPTS} ${APP_ARGUMENTS} -jar ${REST_WORK}/app.jar
