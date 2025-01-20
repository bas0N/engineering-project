//import { ReactNode } from "react";

declare module 'authComponents/AuthProvider' {
    export const useAuth: () => {
        token: string,
        logout: () => void
    };
    export const AuthProvider;
}
declare module 'authComponents/SignIn'
declare module 'authComponents/SignUp'
declare module 'userSettings/UserSettings'
declare module 'userBasket/UserBasket'
declare module 'productsBrowsing/Tiles'
declare module 'productsBrowsing/Product'
declare module 'productsManaging/AddProduct'
declare module 'productsManaging/ProductsList'
declare module 'userOrder/UserOrder'
declare module 'userOrder/OrderHistory'