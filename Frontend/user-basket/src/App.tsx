import { BasketContainer, BasketHeader, BasketWrapper } from './App.styled'
import { BasketSummary } from './components/BasketSummary/BasketSummary.tsx';
import { BasketItems } from './components/BasketItems/BasketItems.tsx';
import { useTranslation } from 'react-i18next';
import './i18n/i18n.tsx';

function Basket() {

  const {t} = useTranslation();

  return (
    <BasketWrapper>
      <BasketHeader>
        {t('basket.basketHeader')}
      </BasketHeader>
      <BasketContainer>
        <BasketItems />
        <BasketSummary />
      </BasketContainer>
    </BasketWrapper>
  )
}

export default Basket;
