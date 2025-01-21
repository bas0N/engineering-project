import React, { Suspense } from 'react'
import ReactDOM from 'react-dom/client'
//import App from './App.tsx'
import './index.css'
import { FluentProvider, webDarkTheme } from '@fluentui/react-components'
import './i18n/i18n.tsx';
import { createBrowserRouter, RouteObject, RouterProvider } from 'react-router-dom'
import { Page404 } from './pages/page404/Page404.tsx'
import { Wrapper } from './main.styled.tsx'
import { Navbar } from './components/navbar/Navbar.tsx';
import './i18n/i18n.tsx'

import { AuthProvider } from 'authComponents/AuthProvider';
import { Preloader } from './components/preloader/Preloader.tsx';
import { PageWrapper } from './Wrapper.tsx'

const SignInPanel = React.lazy(() => import('authComponents/SignIn'));
const SignUpPanel = React.lazy(() => import('authComponents/SignUp'));
const UserSettings = React.lazy(() => import('userSettings/UserSettings'));
const UserBasket = React.lazy(() => import('userBasket/UserBasket'));
const Tiles = React.lazy(() => import('productsBrowsing/Tiles'));
const Product = React.lazy(() => import('productsBrowsing/Product'));
const AddProduct = React.lazy(() => import('productsManaging/AddProduct'));
const ProductsList = React.lazy(() => import('productsManaging/ProductsList'));
const Order = React.lazy(() => import('userOrder/UserOrder'));
const OrderHistory = React.lazy(() => import('userOrder/OrderHistory'));
const ProductsLikes = React.lazy(() => import('productsManaging/ProductsLikes'));

const routes: RouteObject[] = [
  {
    path: '/',
    element: <SignInPanel />
  },
  {
    path: '/signin',
    element: <SignInPanel />
  },
  {
    path: '/signup',
    element: <SignUpPanel />
  },
  {
    path: '/products/:productId',
    element: <Product />
  },
  {
    path: '/products/search/:query',
    element: <Tiles />
  },
  {
    path: '/settings/',
    element: <UserSettings />
  },
  {
    path: '/basket/',
    element: <UserBasket />
  },
  {
    path: '/products/add',
    element: <AddProduct />
  }, 
  {
    path: '/products/mine',
    element: <ProductsList />
  },
  {
    path: '/order/',
    element: <Order />
  },
  {
    path: '/order-history/',
    element: <OrderHistory />
  },
  {
    path: '/products/likes',
    element: <ProductsLikes />
  },
  {
    path: '*',
    element: <Page404 />
  },

];

const router = createBrowserRouter(routes.map((route) => ({
  ...route,
  element: <PageWrapper>
    <Navbar />
    {route.element}
  </PageWrapper>
})))


ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <FluentProvider theme={webDarkTheme}>
      <Suspense fallback={<Preloader />}>
        <AuthProvider>
          <Wrapper>
            <RouterProvider router={router} />
          </Wrapper>
        </AuthProvider>
      </Suspense>
    </FluentProvider>
  </React.StrictMode>,
)
