import { DeleteRegular } from '@fluentui/react-icons';
import { Image, Text, Tooltip } from '@fluentui/react-components';
import { 
    BasketItemsWrapper, 
    BasketItem,
    BasketItemDescription,
    BasketItemManagement,
    BasketItemPrice,
    BasketItemTitle,
    BasketItemQuantity,
    BasketItemDeleteButton,
} from './BasketItems.styled.tsx';
import { useTranslation } from 'react-i18next';

export type BasketItemType = {
    uuid: string;
    name: string;
    image: string;
    quantity: number;
    summaryPrice: number;
};

interface BasketItemsProps {
    items: BasketItemType[];
}

export const BasketItems = ({
    items
}: BasketItemsProps) => {

    const {t} = useTranslation();

    return (<BasketItemsWrapper>
        {items.length > 0 ?
        items.map((item, ind) => <BasketItem 
            key={`basket-item-${ind}`}>
                <BasketItemDescription>
                    <Image src={item.image} />
                    {item.name.length > 32 ? (
                        <Tooltip content={item.name} relationship='description'>
                            <BasketItemTitle size={500}>
                                {item.name}
                            </BasketItemTitle>
                        </Tooltip>)
                        : (
                        <BasketItemTitle size={500}>
                            {item.name}
                        </BasketItemTitle>
                    )}
                    <BasketItemPrice>
                        {item.summaryPrice}$
                    </BasketItemPrice>
                </BasketItemDescription>
                <BasketItemManagement>
                    <BasketItemQuantity type='number' value={item.quantity.toString()} />
                    <BasketItemDeleteButton>
                        <DeleteRegular />
                    </BasketItemDeleteButton>
                </BasketItemManagement>
            </BasketItem>
        )
    : <Text size={600} align='center'>{t('basket.noItems')}</Text>}
    </BasketItemsWrapper>);
}