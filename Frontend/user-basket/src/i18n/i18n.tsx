import i18next from "i18next";
import Backend from "i18next-http-backend";
import { initReactI18next } from "react-i18next";

if(!import.meta.env.VITE_BLOCK_I18NEXT){
  i18next
    .use(initReactI18next)
    .use(Backend).init({
    fallbackLng: 'en',
    backend: {
      loadPath: '/locales/translation.{{lng}}.json'
    }
  });
}

export default i18next;