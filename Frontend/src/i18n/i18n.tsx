import i18next from "i18next";
import { initReactI18next } from "react-i18next";

i18next.use(initReactI18next).init({
  lng: 'en-US',
  resources: {
    'en-US': {
      translation: {
        "authCard": {
          "email": "Email",
          "emailLabel": "Prompt the email...",
          "goToSignIn": "I have the account",
          "goToSignUp": "I don't have an account",
          "password": "Password",
          "passwordRep": "Repeat password",
          "passwordRepLabel": "Repeat your password",
          "passwordLabel": "Enter the password...",
          "signInButton": "Sign in",
          "signInHeader": "Sign in",
          "signUpHeader": "Sign up",
          "signUpButton": "Sign up",
        }
      }
    }
  }
});

export default i18next;