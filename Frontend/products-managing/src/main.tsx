import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { FluentProvider, webDarkTheme } from '@fluentui/react-components';
import './index.css'
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import { AddProduct } from './addProduct/AddProduct.tsx';

const router = createBrowserRouter([{
  path: '/product/add',
  element: <AddProduct />
}])

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <FluentProvider theme={webDarkTheme}>
      <RouterProvider router={router}/>
    </FluentProvider>
  </StrictMode>,
)
