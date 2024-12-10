import { BasketContainer, BasketHeader, BasketWrapper, LoadingSpinner } from './App.styled'
import { BasketSummary } from './components/BasketSummary/BasketSummary';
import { BasketItems, BasketItemType } from './components/BasketItems/BasketItems';
import { useTranslation } from 'react-i18next';
import axios from 'axios';
import { useCallback, useEffect, useState } from 'react';
import { Toast, ToastTitle, Text, useToastController } from '@fluentui/react-components';

export default function Basket() {

  const {t} = useTranslation();
  const toasterId = import.meta.env.VITE_PREVIEW_MODE ? 'localToaster' : 'localToaster';
  console.log(toasterId)
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

    } catch (error){
      console.log(error);
      dispatchToast(<Toast>
        <ToastTitle>{t('basket.somethingWentWrong')}</ToastTitle>
      </Toast>, {position: 'top-end', intent: 'error'});
      setLoadingFailed(true);
    }
  }, [dispatchToast, t, token])

  useEffect(() => {
    getBasketStuff();
  }, [getBasketStuff]);

  const deleteItemCallback = async(itemId: string) => {
    try {
      await axios.delete(`${import.meta.env.VITE_API_URL}basket`, {
        data: {
          basketItemUuid: itemId,
          quantity: (basketItems as BasketItemType[]).find((elem) => elem.uuid === itemId).quantity
        },
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      await getBasketStuff();
    } catch {
      dispatchToast(<Toast>
        <ToastTitle>{t('basket.failedToDelete')}</ToastTitle>
      </Toast>, {position: 'top-end', intent: 'error'});
    }
  };

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
            <BasketItems 
              items={basketItems as BasketItemType[]} 
              deleteItemCallback={deleteItemCallback}
            />
            <BasketSummary orderValue={basketPrice} /> 
          </>)
          : (<LoadingSpinner label={t('basket.basketLoading')} /> )
        }
      </BasketContainer>
    </BasketWrapper>
  )
}