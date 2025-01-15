import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import federation from '@originjs/vite-plugin-federation'

// https://vite.dev/config/
export default defineConfig({
  base: '/assets',
  plugins: [
    react(),
    federation({
      name: 'productsManagingMicrofrontend',
      filename: 'remoteProductsManagingEntry.js',
      exposes: {
        './AddProduct': './src/addProduct/AddProduct.tsx',
        './ProductsList': './src/productsList/ProductsList.tsx',
      },
      shared: ['react', 'react-dom', 'react-router-dom', 'styled-components', 'react-i18next', '@fluentui/react-components'],
    })
  ],
  preview: {
    port: 4178
  },
  build: {
    modulePreload: false,
    target: "esnext",
    minify: false,
    cssCodeSplit: false,
  },
})
