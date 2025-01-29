import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import { FluentProvider, webDarkTheme } from '@fluentui/react-components'

const router = createBrowserRouter([{
  path: '/',
  element: <App />
}]);

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <FluentProvider theme={webDarkTheme}>
      <RouterProvider router={router} />
    </FluentProvider>
  </StrictMode>,
)
