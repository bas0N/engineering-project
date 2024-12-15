import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import federation from "@originjs/vite-plugin-federation"


// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    react(),
    federation({
      name: 'engineering-project',
      remotes: {
        authComponents: 'http://localhost:4173/assets/remoteAuthEntry.js',
        userSettings: 'http://localhost:4174/assets/remoteUserSettingsEntry.js',
        userBasket: 'http://localhost:4175/assets/remoteUserBasketEntry.js',
        productsBrowsing: 'http://localhost:4176/assets/remoteProductsBrowsingEntry.js',
        userOrder: 'http://localhost:4177/assets/remoteUserOrderEntry.js'
      },
      shared: ['react', 'react-dom', 'react-router-dom', '@fluentui/react-components', 'react-i18next']
    })
  ],
})
