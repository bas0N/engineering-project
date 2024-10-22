import { Button, CardHeader, Link } from '@fluentui/react-components'
import { AuthCard, AuthCardFooter, AuthCardHeader, AuthCardPreview, AuthInput } from '../../App.styled'
import { useState } from 'react'
import { useTranslation } from '../../../node_modules/react-i18next';
import { useAuth } from '../../contexts/authContext';
import { Navigate } from 'react-router-dom';

import '../../i18n/i18n.tsx';

export const SignInPanel = () => {

  const {t} = useTranslation();
  const {token} = useAuth();

  const [email, setEmail] = useState('');
  const [passwd, setPasswd] = useState('');

  if(token !== null){
    return <Navigate to="/" />;
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
              onChange={(e) => setEmail(e.currentTarget.value)}
            />
            <AuthInput 
              placeholder={`${t('authCard.password')}...`}
              aria-label={t('authCard.passwordLabel')}
              type='password' 
              value={passwd}
              onChange={(e) => setPasswd(e.currentTarget.value)}
            />
        </AuthCardPreview>
        <AuthCardFooter>
          <Button>
            {t('authCard.signInButton')}
          </Button>
          <Link href="/signup">{t('authCard.goToSignUp')}</Link>
        </AuthCardFooter>
      </AuthCard>
    </>
  )
};

export default SignInPanel;