import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { RouteObject, RouterProvider, createBrowserRouter } from 'react-router-dom';
import { FluentProvider, webDarkTheme } from '@fluentui/react-components';
import './index.css'
import Product from './pages/product/Product.tsx'
import Tiles from './pages/tiles/Tiles.tsx'
import './i18n/i18n.tsx'

const routes: RouteObject[] = [
  {
    path: '/products/:productId',
    element: <Product />
  },
  {
    path: '/products/search/:query',
    element: <Tiles />
  },
];

const router = createBrowserRouter(routes);

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <FluentProvider theme={webDarkTheme}>
      <RouterProvider router={router} />
    </FluentProvider>
  </StrictMode>,
)
