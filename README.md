## Table of Contents

* [About the Service](#about-the-service)
* [History](#history)
* [Getting Started](#getting-started)
    * [Tech Stack](#tech-stack)
    * [Prerequisites](#prerequisites)
* [Configurations](#configurations)
    * [Database](#database-configurations)    
* [Usage](#usage)
    * [Authenticate](#authenticate)
* [Run-Build & Test](#run-build--test)
    * [Maven](#maven)
    * [Swagger UI](#swagger)		

---
## About the Service

Simple application where football/soccer fans will create fantasy teams and will be able to sell or buy players. 

---
## History

Version : <b>v1</b> Initial version of service.

---
## Getting Started


### Tech Stack

- Java 8
- Spring Framework
- Spring Boot
- Mysql  
- JUnit

---

### Prerequisites

The microservice runs with Java 8. It requires MYSQL. Maven needed to build service.

---
### Configurations

### Database Configurations

Locale database configurations for MYSQL Server. <b>All configuration values should replace with your MYSQL server information.</b>

* `spring.datasource.url` : Default database host is "jdbc:mysql://localhost:3306/soccer_management?createDatabaseIfNotExist=true&autoReconnect=true&useSSL=false". 
* `spring.datasource.username` : Default database username is "root". 
* `spring.datasource.password` : Default database password is "123456". 

---
## Usage

API usage and sample request can be accessed from SoccerManagerAPI.postman_collection.json file. You can also access detailed information about api via swagger.

---
### Authenticate

Service endpoints secured. Before sending requests, JWT needs to be add as a bearer token to the header parameters.

---
#### Credentials
Default admin user is; <b>admin@mail.com</b>, password: <b>123456</b>

Default user is; <b>user1@mail.com</b>, password: <b>123456</b>

---
## Run-Build & Test

#### Maven

For maven usage just run "runMaven.sh" or

```ssh
$ mvn clean install
$ mvn spring-boot:run
```
*Default {$PORT} of service is : 8080*

---
#### Test

For start integration test run "runIntegrationTest.sh" or

```ssh
$ mvn clean package
```
After that test report will be created under /target/site/jacoco folder.

---
#### Swagger

There is swagger in the service, it can be accessed as follows after the application runs for testing. 

`http://localhost:8080/swagger-ui/`