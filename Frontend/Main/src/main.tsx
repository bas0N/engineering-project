import React, { Suspense } from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.tsx'
import './index.css'
import { FluentProvider, webDarkTheme } from '@fluentui/react-components'
import './i18n/i18n.tsx';
import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import { Page404 } from './pages/page404/Page404.tsx'
import { Wrapper } from './main.styled.tsx'
import { Product } from './pages/product/Product.tsx'
import { Tiles } from './pages/tiles/Tiles.tsx'

import {AuthProvider} from 'authComponents/AuthProvider';

const SignInPanel = React.lazy(() => import('authComponents/SignIn'));
const SignUpPanel = React.lazy(() => import('authComponents/SignUp'));

const router = createBrowserRouter([
  {
    path: '/',
    element: <App />
  },
  {
    path: '/signin',
    element: <Suspense fallback={<div>Loading...</div>}>
      <SignInPanel />
    </Suspense>
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
    path: '*',
    element: <Page404 />
  }
])

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <FluentProvider theme={webDarkTheme}>
      <Suspense fallback={<div>Loading...</div>}>
        <AuthProvider>
          <Wrapper>
            <RouterProvider router={router} />
          </Wrapper>
        </AuthProvider>
      </Suspense>
    </FluentProvider>
  </React.StrictMode>,
)
