import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import federation from '@originjs/vite-plugin-federation'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react(),
    federation({
      name: 'userOrderMicrofrontend',
      filename: 'remoteUserOrderEntry.js',
      exposes: {
        './UserOrder': './src/App.tsx'
      },
      shared: ['react', 'react-dom', 'react-router-dom', 'react-i18next', '@fluentui/react-components']
    })
  ],
  preview: {
    port: 4177,
  },
  build: {
    modulePreload: false,
    target: "esnext",
    minify: false,
    cssCodeSplit: false,
  },
})
