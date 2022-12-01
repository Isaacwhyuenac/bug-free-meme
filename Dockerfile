FROM gradle:jdk11

WORKDIR /usr/app/

COPY . .

RUN ./gradlew build

CMD [ "java", "-jar", "./build/libs/price-service-0.0.1-SNAPSHOT.jar" ]
