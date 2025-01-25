# Distributed Recommendation System for Enhanced User Experience in Client Applications

This project is the Engineer thesis of Wojciech Basiński, Szymon Kupisz, and Jakub Oganowski, conducted under the help of Grzegorz Ostrek, PhD. All rights reserved.

## Application Setup

### Frontend
- Requirements:
1. NPM installed, version 10.1.0 or newer
2. Node.JS installed, version 20.9.0 or newer

- Installation process:
1. `cd Main`
2. `npm i`
3. `cd ../auth-module`
4. `npm i `
5. `npm run build`
6. `cd ../products-browsing`
7. `npm i `
8. `npm run build`
9. `cd ../products-managing`
10. `npm i `
11. `npm run build`
12. `cd ../user-settings`
13. `npm i `
14. `npm run build`
15. `cd ../user-basket`
16. `npm i `
17. `npm run build`
18. `cd ../user-order`
19. `npm i `
20. `npm run build`

- Running the frontend part:
**Note:** To run the application correctly, the environmental variables, which due to the security concerns were not included in this repo, are needed. To obtain them, please reach the authors of this repo out.

To run the frontend part, at least 7 terminal tabs are required to be up.
In the first six ones, for each of the microfrontends present i.e. `auth-module`, `products-browsing`, `products-managing`, `user-settings`, `user-basket`, `user-order`, execute `cd` command and run `npm run preview`.
Then, in the last terminal tab remaining, type the following commands:
1. `cd Main`
2. `npm run dev`

If the application setup was correct, the web application should be available at `http://localhost:5173`.