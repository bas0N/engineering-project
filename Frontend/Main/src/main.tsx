import React, { Suspense } from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.tsx'
import './index.css'
import { FluentProvider, webDarkTheme } from '@fluentui/react-components'
import './i18n/i18n.tsx';
import { createBrowserRouter, RouteObject, RouterProvider } from 'react-router-dom'
import { Page404 } from './pages/page404/Page404.tsx'
import { Wrapper } from './main.styled.tsx'
import { Product } from './pages/product/Product.tsx'
import { Tiles } from './pages/tiles/Tiles.tsx'
import { Navbar } from './components/navbar/Navbar.tsx';

import { AuthProvider } from 'authComponents/AuthProvider';
import { Preloader } from './components/preloader/Preloader.tsx';

const SignInPanel = React.lazy(() => import('authComponents/SignIn'));
const SignUpPanel = React.lazy(() => import('authComponents/SignUp'));
const UserSettings = React.lazy(() => import('userSettings/UserSettings'));

const routes: RouteObject[] = [
  {
    path: '/',
    element: <App />
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
    path: '*',
    element: <Page404 />
  }
];

const router = createBrowserRouter(routes.map((route) => ({
  ...route,
  element: <>
    <Navbar />
    {route.element}
  </>
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
