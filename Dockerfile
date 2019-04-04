FROM openjdk:8-jdk

COPY . /doublewords
WORKDIR /doublewords

RUN ./gradlew clean shadowJar

FROM openjdk:8-jre-alpine

COPY --from=0 /doublewords/build/libs/doublewords-all.jar /
COPY ./docker-entrypoint.sh /
RUN chmod +x /docker-entrypoint.sh

WORKDIR /

CMD '/docker-entrypoint.sh'
