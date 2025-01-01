import { BasketItem } from "../../Order.types";
import { Text } from "@fluentui/react-components";
import {
    BasketItemDetails,
    BasketItemWrapper,
    BasketItemImage, 
    BasketItemListContainer,
    BasketItemHeader
} from "./BaksteItemList.styled";
import { useTranslation } from "react-i18next";

interface BasketItemsListProps {
    items: BasketItem[];
}

export function BasketItemsList({ items }: BasketItemsListProps) {
    const {t} = useTranslation();

    return (
        <BasketItemListContainer>
            <BasketItemHeader>{t('basketItemList.title')}</BasketItemHeader>
            {items.map(item => (
                <BasketItemWrapper key={item.uuid}>
                    <BasketItemImage src={item.imageUrl} alt={item.name}/>
                    <BasketItemDetails>
                        <Text>{item.name}</Text>
                        <Text>{t('basketItemList.quantity')}: {item.quantity}</Text>
                        <Text>{t('basketItemList.price')}: {item.summaryPrice} PLN</Text>
                    </BasketItemDetails>
                </BasketItemWrapper>
            ))}
        </BasketItemListContainer>
    );
}
