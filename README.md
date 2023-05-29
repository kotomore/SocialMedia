# Social Media API

## Based
#### Java + Spring Boot + Spring Security + JWT + PostgreSQL


## Swagger
http://localhost:8080/swagger-ui/index.html#/


## Install && launch

<b>With Docker</b>
```
git clone https://github.com/kotomore/SocialMedia.git
cd SocialMedia
mvn package
docker-compose up
```

<b>Without Docker</b>
#### Requirements: Java 17, PostgreSQL 15


Create postgres DB with name `postgres`.
Change your DB username & password in `application.properties` file


```
git clone https://github.com/kotomore/SocialMedia.git
cd SocialMedia
mvn package
java -jar target/SocialMedia-0.0.1-SNAPSHOT.jar
```
