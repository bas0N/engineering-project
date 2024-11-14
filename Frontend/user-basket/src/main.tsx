import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import Basket from './App.tsx'
import { FluentProvider, webDarkTheme } from '@fluentui/react-components'
import './i18n/i18n.tsx'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <FluentProvider theme={webDarkTheme}>
      <Basket />
    </FluentProvider>
  </StrictMode>,
)
