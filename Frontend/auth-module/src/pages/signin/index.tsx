import { Button, CardHeader, Link, Text } from '@fluentui/react-components'
import { AuthCard, AuthCardFooter, AuthCardHeader, AuthCardPreview, AuthInput } from '../../App.styled'
import { ChangeEvent, useState } from 'react'
import { useTranslation } from 'react-i18next';
import { useAuth } from '../../contexts/authContext';
import { Navigate } from 'react-router-dom';
import '../../i18n/i18n'

import axios from 'axios';

export const SignInPanel = () => {

  const {t} = useTranslation();
  const {token, login} = useAuth();

  const [email, setEmail] = useState('');
  const [passwd, setPasswd] = useState('');
  const [loginFailed, setLoginFailed] = useState(false);

  if(token !== null){
    localStorage.setItem('redirect','/');
    window.dispatchEvent(new CustomEvent('redirect'));
    if(import.meta.env.VITE_PREVIEW_MODE){
      return <Navigate to="/"/>;
    }
  }

  const onSigninClicked = async () => {
    setLoginFailed(false);
    const address = import.meta.env.VITE_API_URL+'auth/login';
    try {
      const results = await axios.post(address, {
        email,
        password: passwd,
      }, {
        withCredentials: true
      });
      login(results.data.token, results.data.refreshToken);
      localStorage.setItem('redirect','/');
      window.dispatchEvent(new CustomEvent('redirect'));
      if(import.meta.env.VITE_PREVIEW_MODE){
        return <Navigate to="/"/>;
      }
      
    } catch {
      setLoginFailed(true);
    }
  }

  return (
    <>
      <AuthCard>
        <CardHeader header={
          <AuthCardHeader size={500}>{t('authCard.signInHeader')}</AuthCardHeader>
        }/>
        <AuthCardPreview>
            <AuthInput 
              placeholder={`${t('authCard.email')}...`}
              aria-label={t('authCard.emailLabel')}
              type='email' 
              value={email}
              onChange={(e: ChangeEvent<HTMLInputElement>) => setEmail(e.currentTarget.value)}
            />
            <AuthInput 
              placeholder={`${t('authCard.password')}...`}
              aria-label={t('authCard.passwordLabel')}
              type='password' 
              value={passwd}
              onChange={(e: ChangeEvent<HTMLInputElement>) => setPasswd(e.currentTarget.value)}
            />
            {
              loginFailed && <Text size={400} align='center' style={{color: 'red'}}>
                {t('authCard.failureMessage')}
              </Text>
            }
        </AuthCardPreview>
        <AuthCardFooter>
          <Button onClick={onSigninClicked}>
            {t('authCard.signInButton')}
          </Button>
          <Link href="/signup">{t('authCard.goToSignUp')}</Link>
        </AuthCardFooter>
      </AuthCard>
    </>
  )
};

export default SignInPanel;