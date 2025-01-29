# Distributed Recommendation System for Enhanced User Experience in Client Applications

This project is the Engineer thesis of Wojciech Basiński, Szymon Kupisz, and Jakub Oganowski, conducted under the help of Grzegorz Ostrek, PhD. All rights reserved.

## Application Setup

### Frontend
- Requirements:
1. NPM installed, version 10.1.0 or newer
2. Node.JS installed, version 20.9.0 or newer

#### Main application

- Installation process:
1. `cd Main`
2. `npm install`
3. `cd ../auth-module`
4. `npm install `
5. `npm run build`
6. `cd ../products-browsing`
7. `npm install `
8. `npm run build`
9. `cd ../products-managing`
10. `npm install `
11. `npm run build`
12. `cd ../user-settings`
13. `npm install `
14. `npm run build`
15. `cd ../user-basket`
16. `npm install `
17. `npm run build`
18. `cd ../user-order`
19. `npm install `
20. `npm run build`

- Running the frontend part:
**Note:** To run the application correctly, the environmental variables, which due to the security concerns were not included in this repo, are needed. To obtain them, please reach the authors of this repo out.

To run the frontend part, at least 7 terminal tabs are required to be up.
In the first six ones, for each of the microfrontends present i.e. `auth-module`, `products-browsing`, `products-managing`, `user-settings`, `user-basket`, `user-order`, execute `cd` command and run `npm run preview`.
Then, in the last terminal tab remaining, type the following commands:
1. `cd Main`
2. `npm run dev`

If the application setup was correct, the web application should be available at `http://localhost:5173`.

#### Administrator app

- Instalation process: 
1. `cd Admin`
2. `npm install`

To run the application type `npm run dev` in your terminal. The application should be available at `http://localhost:5174`.

# Backend Installation
## 1. Install java 
sudo apt install openjdk-21-jdk -y
## 2. Verify Java version 
java --version
## 3. Expected Output
openjdk version "21.0.x" ...
## 4.Install maven 
## sudo apt install maven -y
## 5.Verify maven installation
mvn -version
## 6.Install Docker
sudo apt install docker.io -y
## 7.Start and enable docker
sudo systemctl start docker
sudo systemctl enable docker
## 8.Verify the installation:
docker --version
## 9.Install Docker Compose
sudo apt install docker-compose -y
docker-compose --version

## 10.Inside /Backend/JavaBackend folder run
mvn clean install -DskipTests

## 11. Set Up Environment Variables, inside folder /Backend create .env file
nano .env

## 12. Paste this into .env file
# Security
JWT_SECRET=VGhpcy1pcy1hbi1leGFtcGxlLWJhc2U2NC1zdHJpbmc=

# Service Discovery
EUREKA_URL=http://eureka-service:8761/eureka/

# Databases
SPRING_DATASOURCE_URL_AUTH=jdbc:postgresql://postgres-db-auth:5432/auth_db
SPRING_DATASOURCE_URL_BASKET=jdbc:postgresql://postgres-db-basket:5432/basket_db
SPRING_DATASOURCE_URL_LIKE=jdbc:postgresql://postgres-db-like:5432/like_db
SPRING_DATASOURCE_URL_ORDER=jdbc:postgresql://postgres-db-order:5432/order_db

SPRING_DATASOURCE_USERNAME=example_user
SPRING_DATASOURCE_PASSWORD=example_password

# Message Broker
KAFKA_BROKER=kafka:9092

# MongoDB
MONGO_URI=mongodb+srv://admin:examplepassword@engineering-proj-dev.a7fitxf.mongodb.net/dev?retryWrites=true&w=majority&appName=engineering-proj-dev
MONGO_DB=dev

# Cloud Storage
CLOUDINARY_CLOUD_NAME=example-cloud
CLOUDINARY_API_KEY=123456789012345
CLOUDINARY_API_SECRET=example-secret-key

# API Gateway
API_GATEWAY_URL=http://gateway-service:8888/api/v1/gateway

# Application Ports
PORT=5000
CHROMA_HOST=chroma

# Stripe Payment Gateway
STRIPE_API_KEY=sk_test_4eC39HqLyjWDarjtT1zdp7dc
STRIPE_ENDPOINT_SECRET=whsec_exampleSecretKey

# Spring Mail Configuration
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=example@gmail.com
SPRING_MAIL_PASSWORD="example-app-password"
SPRING_MAIL_SMTP_AUTH=true
SPRING_MAIL_SMTP_STARTTLS_ENABLE=true
## 13. Run in Backend folder:
docker-compose --env-file .env up --build -d
