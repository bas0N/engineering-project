/* istanbul ignore file */
import i18next from "i18next";
import Backend from "i18next-http-backend";
import { initReactI18next } from "react-i18next";

i18next
  .use(initReactI18next)
  .use(Backend).init({
  supportedLngs: ['en', 'it', 'de', 'pl'],
  fallbackLng: 'en',
  backend: {
    loadPath: '/locales/translation.{{lng}}.json'
  }
});

export default i18next;