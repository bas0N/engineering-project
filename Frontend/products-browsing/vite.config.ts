import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import federation from '@originjs/vite-plugin-federation'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react(),
    federation({
      name: 'productsBrowsingMicrofrontend',
      filename: 'remoteProductsBrowsingEntry.js',
      exposes: {
        './Product': './src/pages/product/Product.tsx',
        './Tiles': './src/pages/tiles/Tiles.tsx',
      },
      shared: ['react', 'react-dom', 'react-router-dom', 'styled-components', 'react-i18next', '@fluentui/react-components']
    })
  ],
  preview: {
    port: 4176
  },
  build: {
    modulePreload: false,
    target: "esnext",
    minify: false,
    cssCodeSplit: false,
  },
})
