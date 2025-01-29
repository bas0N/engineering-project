import { BasketContainer, BasketHeader, BasketWrapper, LoadingSpinner } from './App.styled'
import { BasketSummary } from './components/BasketSummary/BasketSummary';
import { BasketItems, BasketItemType } from './components/BasketItems/BasketItems';
import { useTranslation } from 'react-i18next';
import axios from 'axios';
import { useCallback, useEffect, useState } from 'react';
import { Toast, ToastTitle, Text, useToastController } from '@fluentui/react-components';
import './i18n/i18n'

export default function Basket() {

  const {t} = useTranslation();
  const toasterId = import.meta.env.VITE_PREVIEW_MODE ? 'localToaster' : 'mainToaster';
  const token = localStorage.getItem('authToken');
  const [basketItems, setBasketItems] = useState<BasketItemType[]|null>(null);
  const [basketPrice, setBasketPrice] = useState(0);
  const [loadingFailed, setLoadingFailed] = useState(false);
  const [noItems, setNoItems] = useState(false);
  const { dispatchToast } = useToastController(toasterId);

  const getBasketStuff = useCallback(async() => {
    try {
      setNoItems(false);
      setLoadingFailed(false);
      const result = await axios.get(`${import.meta.env.VITE_API_URL}basket`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      if(result.data.basketProducts === null){
        setNoItems(true);
      } else {
        setBasketItems(result.data.basketProducts);
        setBasketPrice(result.data.summaryPrice);
      }

    } catch {
      dispatchToast(<Toast>
        <ToastTitle>{t('basket.somethingWentWrong')}</ToastTitle>
      </Toast>, {position: 'top-end', intent: 'error'});
      setLoadingFailed(true);
    }
  }, [dispatchToast, t, token])

  useEffect(() => {
    const execBasketStuff = async() => {
      await getBasketStuff();
    }
    execBasketStuff();
  }, [getBasketStuff]);

  const deleteItemCallback = async(itemId: string) => {
    try {
      await axios.delete(`${import.meta.env.VITE_API_URL}basket`, {
        data: {
          basketItemUuid: itemId,
          quantity: (basketItems as BasketItemType[]).find((elem) => elem.uuid === itemId)?.quantity ?? 0
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
          : 
          noItems ? (
            <Text size={400} align='center'>{t('basket.noItems')}</Text>
          ) : 
          basketItems !== null ? (<>
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