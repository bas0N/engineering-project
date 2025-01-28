import { Text } from '@fluentui/react-components'
import './App.css'
import { useAuth } from 'authComponents/AuthProvider';
import { useTranslation } from 'react-i18next';
import { Navigate } from 'react-router-dom';
import { AppWrapper } from './App.styled';
import { useEffect } from 'react';
import axios from 'axios';

function App() {

  const {token, logout} = useAuth();
  const {t} = useTranslation();

  const isLoggedIn = token !== null;

  useEffect(() => {
    if(isLoggedIn){
      const validate = async() => {
        try {
          const response = await axios.get(`${import.meta.env.VITE_API_URL}auth/validate`, {
            headers: {
              Authorization: `Bearer ${token}`
            }
          });
          const code = response.data.code as string;
          if(code !== "PERMIT"){
            logout();
            return <Navigate to="/signin" />
          }
        }
        catch {
          logout();
          return <Navigate to="/signin" />
        }
      }
      validate();
    }
  }, [isLoggedIn, logout, token]);

    
  if(!isLoggedIn){
    console.log(Navigate);
    return <Navigate to='/signin' />
  }

  return (
    <AppWrapper>
      <Text as='h2' align='center' size={600} weight='semibold'>{t('landing.header')}</Text>
      <Text>
        {t('landing.desc')}
      </Text>
    </AppWrapper>
  )
}

export default App
