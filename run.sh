export SPRING_DATASOURCE_URL=jdbc:postgresql://asiadb:5432/asiadb
export SPRING_DATASOURCE_USERNAME=admin
export SPRING_DATASOURCE_PASSWORD=password
export SPRING_JPA_HIBERNATE_DDL_AUTO=update
export JWT_SECRET=Kk7y+9VXp2b5q8t/w3E6DcF1AhM4JkLmNoPqRsTuVxYz
export JWT_EXPIRATION_INMILLIS=86400000
./gradlew bootRun -Dspring-boot.run.arguments=--logging.level.org.springframework=DEBUG
