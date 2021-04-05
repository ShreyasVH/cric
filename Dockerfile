FROM mozilla/sbt
MAINTAINER Shreyas
USER root
RUN rm -rf /etc/localtime && ln -s /usr/share/zoneinfo/Asia/Kolkata /etc/localtime
WORKDIR /app
COPY . .
ENTRYPOINT ["sh", "./start.sh"]