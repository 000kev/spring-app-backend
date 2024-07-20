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
- Run the command `docker run -d -e MYSQL_ROOT_PASSWORD=secret -e MYSQL_DATABASE=taskdb --name mysqldb -p 3307:3306 mysql:8.0` as these are the preconfigured settings used in the app.

## Usage


- Run the server using `./mvnw spring-boot:run`

### Endpoints

- POST localhost:7546/auth/register
  This endpoint accepts a username and a password in the body and registers/signs up the user if the user does not already exist.
  It returns the generated JWT token for this user.

  <img width="645" alt="Screenshot 2024-07-19 at 15 51 18" src="https://github.com/user-attachments/assets/0c52355d-cf20-420c-b6fc-041d6c36742d">

- POST localhost:7546/auth/login
  This endpoint accepts a username and a password in the body and logs the user in if the user is registered.
  It returns the generated JWT token for this user.
  <img width="644" alt="Screenshot 2024-07-19 at 15 55 17" src="https://github.com/user-attachments/assets/f0d00f97-597a-4fbf-96bf-36e4f1d73132">
  
- POST (Secure) localhost:7546/team/create
  This endpoint accepts a teamName and a maxMembers in the body and creates a team if it does not already exist. The user that 
  created the team will automoatically be set to the owner of the team
  <img width="627" alt="Screenshot 2024-07-19 at 15 57 42" src="https://github.com/user-attachments/assets/4246d0d3-d2dd-4912-92c7-a05ac13598c3">

  Note that for all Secure endpoints, you need to provide the jwt authorization token returned via login/registration
  <img width="970" alt="Screenshot 2024-07-19 at 15 58 23" src="https://github.com/user-attachments/assets/6f916cf9-265c-4fc6-855d-25ea97bfd01c">

- GET (Secure) localhost:7546/team/viewAll
  This endpoint gets all the teams that have been created in the database
  <img width="537" alt="Screenshot 2024-07-19 at 16 04 32" src="https://github.com/user-attachments/assets/cc2d445e-c864-4603-b2c1-b6711faf0278">


- GET (Secure) localhost:7546/team/view/{teamName}
  This endpoint gets all the users that are part of a specific team if it exists
  <img width="532" alt="Screenshot 2024-07-19 at 16 04 40" src="https://github.com/user-attachments/assets/6ca7c77a-6672-4802-9a94-655783dc1326">

- POST (Secure) localhost:7546/team/request/{teamName}
  This endpoint allows a user who is not already part of the team to make a request to join a specific team if it exists
  <img width="399" alt="Screenshot 2024-07-19 at 16 08 37" src="https://github.com/user-attachments/assets/7dc53de8-a6ca-4ca2-9f01-eae05cbdebfa">

- GET (Secure, Role Access) localhost:7546/team/request/{teamName}
  This endpoint allows an authenticated user who is the team owner and a team leader to view all the requests for that team
  <img width="368" alt="Screenshot 2024-07-19 at 16 08 55" src="https://github.com/user-attachments/assets/e2263aa0-bd06-4a5d-bf69-e915a54006bd">


- POST (Secure, Role Access) localhost:7546/team/request/decline/{teamName}/{username}
  This endpoint allows the authenticated team owner only to reject requests
  <img width="584" alt="Screenshot 2024-07-19 at 16 12 59" src="https://github.com/user-attachments/assets/8c1d2fa5-62c4-4204-9a2a-1ae5478d9a44">


- POST (Secure, Role Access) localhost:7546/team/request/accept/{teamName}/{username}
  This endpoint allows the authenticated team owner only to accept requests and add them to the team
  <img width="570" alt="Screenshot 2024-07-19 at 16 13 02" src="https://github.com/user-attachments/assets/0dd1c952-bc1c-4908-aecc-314c53a69fe1">

- POST (Secure, Role Access) localhost:7546/edit/{teamName}
  This endpoint accepts either teamName or maxMembers or both in the body and updates the team details with the non-null values

- POST (Secure, Role Access) localhost:7546/remove/{teamName}/{username}
  This endpoint allows an authorized team owner to remove a team member from their team

- DELETE (Secure, Role Access) localhost:7546/delete/{teamName}
  This endpoint allows an authorized team owner to delete the team






