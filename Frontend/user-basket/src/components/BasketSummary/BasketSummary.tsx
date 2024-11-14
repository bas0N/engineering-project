import { Text } from '@fluentui/react-components';
import { 
    BasketSummaryWrapper, 
    BasketSummaryItem, 
    BasketSummaryDivider,
    BasketSummaryBtn 
} from './BasketSummary.styled';
import { useTranslation } from 'react-i18next';

export const BasketSummary = () => {

    const {t} = useTranslation();

    return (
        <BasketSummaryWrapper>
            <Text as='h2' size={500}>{t('basket.summary.title')}</Text>
            <BasketSummaryItem>
                <Text weight='semibold' size={400}>{t('basket.summary.order')}</Text>
                <Text size={400}>123.24$</Text>
            </BasketSummaryItem>
            <BasketSummaryItem>
                <Text weight='semibold' size={400}>{t('basket.summary.delivery')}</Text>
                <Text size={400}>Free</Text>
            </BasketSummaryItem>
            <BasketSummaryItem>
                <Text weight='semibold' size={400}>{t('basket.summary.total')}</Text>
                <Text size={400}>123.24$</Text>
            </BasketSummaryItem>
            <BasketSummaryDivider />
            <BasketSummaryBtn>
                {t('basket.summary.buy')}
            </BasketSummaryBtn>
        </BasketSummaryWrapper>
    );
}