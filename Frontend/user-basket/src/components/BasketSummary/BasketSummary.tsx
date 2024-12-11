import { Text } from '@fluentui/react-components';
import { 
    BasketSummaryWrapper, 
    BasketSummaryItem, 
    BasketSummaryDivider,
    BasketSummaryBtn 
} from './BasketSummary.styled';
import { useTranslation } from 'react-i18next';
import {useNavigate} from "react-router-dom";

interface BasketSummaryProps {
    orderValue: number;
}

export const BasketSummary = ({
    orderValue
}: BasketSummaryProps) => {

    const {t} = useTranslation();
    const navigate = useNavigate();

    const handleBuyClick = () =>{
        navigate('/order');
    }

    return (
        <BasketSummaryWrapper>
            <Text as='h2' size={500}>{t('basket.summary.title')}</Text>
            <BasketSummaryItem>
                <Text weight='semibold' size={400}>{t('basket.summary.order')}</Text>
                <Text size={400}>{orderValue}$</Text>
            </BasketSummaryItem>
            <BasketSummaryItem>
                <Text weight='semibold' size={400}>{t('basket.summary.delivery')}</Text>
                <Text size={400}>Free</Text>
            </BasketSummaryItem>
            <BasketSummaryItem>
                <Text weight='semibold' size={400}>{t('basket.summary.total')}</Text>
                <Text size={400}>{orderValue}$</Text>
            </BasketSummaryItem>
            <BasketSummaryDivider />
            <BasketSummaryBtn onClick = {handleBuyClick}>
                Go to checkout
            </BasketSummaryBtn>
        </BasketSummaryWrapper>
    );
}