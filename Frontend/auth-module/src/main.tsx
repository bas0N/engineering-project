import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from './App.tsx'
import './index.css'
import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import { SignInPanel } from './pages/signin/index.tsx'
import { SignUpPanel } from './pages/signup/index.tsx'
import { FluentProvider, webDarkTheme } from '@fluentui/react-components'
import { AuthProvider } from './contexts/authContext.tsx'
import './i18n/i18n.tsx'

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
  }
])

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <FluentProvider theme={webDarkTheme}>
      <AuthProvider>
        <RouterProvider router={router} />
      </AuthProvider>
    </FluentProvider>
  </StrictMode>,
)
