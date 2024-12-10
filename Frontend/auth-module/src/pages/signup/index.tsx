import { Button, CardHeader, Link, Text } from '@fluentui/react-components'
import { AuthCard, AuthCardFooter, AuthCardHeader, AuthCardPreview, AuthInput } from '../../App.styled'
import { useState } from 'react'
import { useTranslation } from '../../../node_modules/react-i18next';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../../contexts/authContext';
import axios from 'axios';

export const SignUpPanel = () => {

  const {t} = useTranslation();
  const {token, login} = useAuth();

  const [email, setEmail] = useState('');
  const [passwd, setPasswd] = useState('');
  const [passwdRep, setPasswdRep] = useState('');
  const [error, setError] = useState(false);

  if(token !== null){
    localStorage.setItem('redirect','/');
    window.dispatchEvent(new CustomEvent('redirect'));
    if(import.meta.env.VITE_PREVIEW_MODE){
      return <Navigate to="/"/>;
    }
  }

  const onRegisterClick = async() => {
    setError(false);
    const address = import.meta.env.VITE_API_URL+'auth/register';
    try {
      const results = await axios.post(address, {
        email,
        password: passwd
      });
      login(results.data.token, results.data.refreshToken);
      localStorage.setItem('redirect','/signin');
      window.dispatchEvent(new CustomEvent('redirect'));

    } catch {
      setError(true);
    }
  };

  return (
    <>
      <AuthCard>
        <CardHeader header={
          <AuthCardHeader size={500}>{t('authCard.signUpHeader')}</AuthCardHeader>
        }/>
        <AuthCardPreview>
            <AuthInput 
              placeholder={`${t('authCard.email')}...`}
              aria-label={t('authCard.emailLabel')}
              type='email' 
              value={email}
              onChange={(e) => setEmail(e.currentTarget.value)}
            />
            <AuthInput 
              placeholder={`${t('authCard.password')}...`}
              aria-label={t('authCard.passwordLabel')}
              type='password' 
              value={passwd}
              onChange={(e) => setPasswd(e.currentTarget.value)}
            />
            <AuthInput 
              placeholder={`${t('authCard.passwordRep')}...`}
              aria-label={t('authCard.passwordRepLabel')}
              type='password' 
              value={passwdRep}
              onChange={(e) => setPasswdRep(e.currentTarget.value)}
            />
            {
              error && <Text align='center' size={400} style={{color: 'red'}}>
                {t('authCard.failureMessage')}
              </Text>
            }
        </AuthCardPreview>
        <AuthCardFooter>
          <Button onClick={() => onRegisterClick()}>
            {t('authCard.signUpButton')}
          </Button>
          <Link href="/signin">{t('authCard.goToSignIn')}</Link>
        </AuthCardFooter>
      </AuthCard>
    </>
  )
};

export default SignUpPanel;
