FROM gradle:jdk13 AS build
COPY --chown=gradle:gradle . /home/gradle/schedge
WORKDIR /home/gradle/schedge

EXPOSE 8080

ENTRYPOINT ["./docker-entry.sh"]