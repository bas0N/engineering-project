import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { FluentProvider, Toaster, webDarkTheme } from '@fluentui/react-components';
import './index.css'
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import AddProduct from './addProduct/AddProduct.tsx';
import ProductsList from './productsList/ProductsList.tsx';

const router = createBrowserRouter([{
  path: 'assets/products/add',
  element: <AddProduct />
}, {
  path: 'assets/products/mine',
  element: <ProductsList />
}])

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <FluentProvider theme={webDarkTheme}>
      <Toaster id='localToaster' />
      <RouterProvider router={router}/>
    </FluentProvider>
  </StrictMode>,
)
