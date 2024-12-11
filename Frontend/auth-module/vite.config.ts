import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import federation from '@originjs/vite-plugin-federation';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    react(),
    federation({
      name: 'authMicrofrontend',
      filename: 'remoteAuthEntry.js',
      exposes: {
        './SignIn': './src/pages/signin/index.tsx',
        './SignUp': './src/pages/signup/index.tsx',
        './AuthProvider': './src/contexts/authContext.tsx'
      },
      shared: ['react', 'react-dom', 'react-router-dom', 'react-i18next', '@fluentui/react-components']
    })
  ],
  build: {
    modulePreload: false,
    target: "esnext",
    minify: false,
    cssCodeSplit: false,
  },
  preview: {
    port: 4173
  }
})
