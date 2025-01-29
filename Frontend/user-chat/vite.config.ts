import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import federation from '@originjs/vite-plugin-federation'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react(),
    federation({
      name: 'userChatMicrofrontend',
      filename: 'remoteUserChatEntry.js',
      exposes: {
        './UserChatList': './src/App.tsx',
        './UserChatDetailPage': './src/components/List/ChatDetail/ChatDetailPage.tsx'
      },
      shared: ['react', 'react-dom', 'react-router-dom', 'react-i18next', '@fluentui/react-components']
    })
  ],
  preview: {
    port: 4178,
  },
  build: {
    modulePreload: false,
    target: "esnext",
    minify: false,
    cssCodeSplit: false,
  },
})
