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
} from './BasketItems.styled';
import { useTranslation } from 'react-i18next';

export type BasketItemType = {
    uuid: string;
    name: string;
    imageUrl: string;
    quantity: number;
    summaryPrice: number;
};

interface BasketItemsProps {
    items: BasketItemType[];
    deleteItemCallback: (id: string) => void;
}

export const BasketItems = ({
    items,
    deleteItemCallback,
}: BasketItemsProps) => {

    const {t} = useTranslation();
    console.log(items);

    return (<BasketItemsWrapper>
        {items.length > 0 ?
        items.map((item, ind) => <BasketItem 
            key={`basket-item-${ind}`}>
                <BasketItemDescription>
                    <Image src={item.imageUrl} alt={item.name} />
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
                    <BasketItemQuantity 
                        aria-label={t('basket.quantityInput')}
                        type='number' 
                        value={item.quantity.toString()} 
                    />
                    <BasketItemDeleteButton 
                        aria-label={t('basket.deleteButton')}
                        onClick={() => deleteItemCallback(item.uuid)}
                    >
                        <DeleteRegular />
                    </BasketItemDeleteButton>
                </BasketItemManagement>
            </BasketItem>
        )
    : <Text size={600} align='center'>{t('basket.noItems')}</Text>}
    </BasketItemsWrapper>);
}