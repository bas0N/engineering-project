import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.tsx'
import './index.css'
import { FluentProvider, webDarkTheme } from '@fluentui/react-components'
import { AuthProvider } from './contexts/authContext.tsx'
import './i18n/i18n.tsx';
import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import { SignInPanel } from './pages/signin/index.tsx'
import { SignUpPanel } from './pages/signup/index.tsx'
import { Page404 } from './pages/page404/Page404.tsx'
import { Wrapper } from './main.styled.tsx'

const router = createBrowserRouter([
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
    path: '*',
    element: <Page404 />
  }
])

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <FluentProvider theme={webDarkTheme}>
      <AuthProvider>
        <Wrapper>
          <RouterProvider router={router} />
        </Wrapper>
      </AuthProvider>
    </FluentProvider>
  </React.StrictMode>,
)
