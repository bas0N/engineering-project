import { BasketContainer, BasketHeader, BasketWrapper, LoadingSpinner } from './App.styled'
import { BasketSummary } from './components/BasketSummary/BasketSummary';
import { BasketItems, BasketItemType } from './components/BasketItems/BasketItems';
import { useTranslation } from 'react-i18next';
import axios from 'axios';
import { useCallback, useEffect, useState } from 'react';
import { Toast, ToastTitle, Text, useToastController } from '@fluentui/react-components';

export default function Basket() {

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
          loadingFailed ?
          (
            <Text>{t('basket.loadingFailed')}</Text>
          )
          : basketItems !== null ? (<>
            <BasketItems items={basketItems as BasketItemType[]} />
            <BasketSummary orderValue={basketPrice} /> 
          </>)
          : (<LoadingSpinner label={t('basket.basketLoading')} /> )
        }
      </BasketContainer>
    </BasketWrapper>
  )
}