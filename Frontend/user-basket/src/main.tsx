import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import Basket from './App.tsx'
import { FluentProvider, Toaster, webDarkTheme } from '@fluentui/react-components'
import './i18n/i18n'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <FluentProvider theme={webDarkTheme}>
      <Toaster toasterId='localToaster' />
      <Basket />
    </FluentProvider>
  </StrictMode>,
)
