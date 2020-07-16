FROM mozilla/sbt
MAINTAINER Shreyas
WORKDIR /app
RUN apt-get update && apt-get -y install dos2unix
RUN apt-get -y install vim
COPY . .
EXPOSE 80
EXPOSE 9059
EXPOSE 8059
EXPOSE 10059
ENTRYPOINT ["sh", "./start.sh"]