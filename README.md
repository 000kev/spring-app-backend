# Spring-app-backend
A simple game monitoring app implemented using Springboot and JWS tokens with Spring Security.
This app is a work-in-progress and is currently focused on exploring the features of SpringBoot
and the implementatin of JWS tokens with Spring Security and secured enpoints. This is the first
iteration of the project only focusing on the backend before integrating the app with the front end
into a single project using Dockerization.

For the moment, feel free to test the enpoints and logic implemented in the backend and explore the 
powerful features of SpringBoot.


## Pre-requisites

In order to use this application;
- You must have Docker installed on your machine and MySQL running `docker pull mysql`
- You must have an HttpClient (Postman)
- Run the command `docker run -d -e MYSQL_ROOT_PASSWORD=secret -e MYSQL_DATABASE=taskdb --name mysqldb -p 3307:3306 mysql:8.0`

