import { Button, CardHeader, Link } from '@fluentui/react-components'
import { AuthCard, AuthCardFooter, AuthCardHeader, AuthCardPreview, AuthInput } from '../../App.styled'
import { useState } from 'react'
import { useTranslation } from 'react-i18next';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../../contexts/authContext';

export const SignUpPanel = () => {

  const {t} = useTranslation();
  const {token} = useAuth();

  const [email, setEmail] = useState('');
  const [passwd, setPasswd] = useState('');
  const [passwdRep, setPasswdRep] = useState('');

  if(token !== null){
    return <Navigate to="/" />;
  }

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
        </AuthCardPreview>
        <AuthCardFooter>
          <Button>
            {t('authCard.signUpButton')}
          </Button>
          <Link href="/signin">{t('authCard.goToSignIn')}</Link>
        </AuthCardFooter>
      </AuthCard>
    </>
  )
};
