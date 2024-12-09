import { BasketContainer, BasketHeader, BasketWrapper, LoadingSpinner } from './App.styled'
import { BasketSummary } from './components/BasketSummary/BasketSummary.tsx';
import { BasketItems, BasketItemType } from './components/BasketItems/BasketItems.tsx';
import { useTranslation } from 'react-i18next';
import './i18n/i18n.tsx';
import axios from 'axios';
import { useCallback, useEffect, useState } from 'react';
import { Toast, ToastTitle, Text, useToastController } from '@fluentui/react-components';

function Basket() {

  const {t} = useTranslation();
  const toasterId = import.meta.env.VITE_PREVIEW_MODE ? 'localToaster' : 'mainToaster';
  const token = localStorage.getItem('authToken');
  const [basketItems, setBasketItems] = useState<BasketItemType[]|null>(null);
  const [basketPrice, setBasketPrice] = useState(0);
  const [loadingFailed, setLoadingFailed] = useState(false);
  const { dispatchToast } = useToastController(toasterId);

  const getBasketStuff = useCallback(async() => {
    try {
      setLoadingFailed(false);
      const result = await axios.get(`${import.meta.env.VITE_API_URL}basket`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      setBasketItems(result.data.basketProducts);
      setBasketPrice(result.data.summaryPrice);

    } catch {
      dispatchToast(<Toast>
        <ToastTitle>{t('basket.somethingWentWrong')}</ToastTitle>
      </Toast>);
      setLoadingFailed(true);
    }
  }, [dispatchToast, t, token])

  useEffect(() => {
    getBasketStuff();
  }, [getBasketStuff]);

  return (
    <BasketWrapper>
      <BasketHeader>
        {t('basket.basketHeader')}
      </BasketHeader>
      <BasketContainer>
        {
          basketItems !== null ? (<>
            <BasketItems items={basketItems as BasketItemType[]} />
            <BasketSummary orderValue={basketPrice} /> 
          </>)
          : !loadingFailed ? (<LoadingSpinner label={t('basket.basketLoading')} /> )
          : (
            <Text>{t('basket.loadingFailed')}</Text>
          )
        }
      </BasketContainer>
    </BasketWrapper>
  )
}

export default Basket;
