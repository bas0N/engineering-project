import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import federation from '@originjs/vite-plugin-federation'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react(),
    federation({
      name: 'userBasketMicrofrontend',
      filename: 'remoteUserBasketEntry.js',
      exposes: {
        './UserBasket': './src/App.tsx'
      },
      shared: ['react', 'react-dom', 'react-router-dom', 'react-i18next', '@fluentui/react-components']
    })
  ],
  preview: {
    port: 4175,
  },
  build: {
    modulePreload: false,
    target: "esnext",
    minify: false,
    cssCodeSplit: false,
  },
})
