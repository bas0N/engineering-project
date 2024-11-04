import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import federation from '@originjs/vite-plugin-federation'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react(),
    federation({
      name: 'user-settings',
      filename: 'remoteUserSettingsEntry.js',
      exposes: {
        './UserSettings': './src/App.tsx'
      },
      shared: ['react', 'react-dom', '@fluentui/react-components', 'react-i18next', 'styled-components']
    })
  ],
  build: {
    modulePreload: false,
    target: "esnext",
    minify: false,
    cssCodeSplit: false,
  },
  preview: {
    port: 4174
  }
})
