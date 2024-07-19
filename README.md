# spring-app-backend
A simple game monitoring app implemented with Springboot and JWS tokens.


## pre-requisites

In order to use this application;
- You must have Docker installed on your machine and MySQL running `docker pull mysql`
- You must have an HttpClient (Postman)
- Run the command `docker run -d -e MYSQL_ROOT_PASSWORD=secret -e MYSQL_DATABASE=taskdb --name mysqldb -p 3307:3306 mysql:8.0`
