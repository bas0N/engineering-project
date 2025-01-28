import { Text } from '@fluentui/react-components'
import './App.css'
import { useAuth } from 'authComponents/AuthProvider';
import { useTranslation } from 'react-i18next';
import { Navigate } from 'react-router-dom';
import { AppWrapper } from './App.styled';

function App() {

  const {token} = useAuth();
  const {t} = useTranslation();

  const isLoggedIn = token === null;
  
  if(!isLoggedIn){
    console.log(Navigate);
    //return <Navigate to='/signin' />
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
