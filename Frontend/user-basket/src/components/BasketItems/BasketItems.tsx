import { useState } from 'react';
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

type ProductImage = {
    thumb: string,
    large: string,
    variant: string,
    hiRes: string | null,
};

type BasketItemType = {
    id: string;
    title: string;
    images: ProductImage[];
    price: string;
};

export const BasketItems = () => {

    const [items] = useState<BasketItemType[]>([{
        id: 'test1',
        title: 'Lorem ipsum dolor sit amet consectetur adipiscing elit',
        images: [{
            thumb: "https://m.media-amazon.com/images/I/519AAiepM1L._SX38_SY50_CR,0,0,38,50_.jpg",
            large: "https://m.media-amazon.com/images/I/519AAiepM1L.jpg",
            variant: "MAIN",
            hiRes: null
        },],
        price: "104.95"
    }]);

    return (<BasketItemsWrapper>
        {items.length > 0 ?
        items.map((item, ind) => <BasketItem 
            key={`basket-item-${ind}`}>
                <BasketItemDescription>
                    <Image src={item.images[0].thumb} />
                    {item.title.length > 32 ? (
                        <Tooltip content={item.title} relationship='description'>
                            <BasketItemTitle size={500}>
                                {item.title}
                            </BasketItemTitle>
                        </Tooltip>)
                        : (
                        <BasketItemTitle size={500}>
                            {item.title}
                        </BasketItemTitle>
                    )}
                    <BasketItemPrice>
                        {item.price}$
                    </BasketItemPrice>
                </BasketItemDescription>
                <BasketItemManagement>
                    <BasketItemQuantity type='number' value={'10'} />
                    <BasketItemDeleteButton>
                        <DeleteRegular />
                    </BasketItemDeleteButton>
                </BasketItemManagement>
            </BasketItem>
        )
    : <Text size={600} align='center'>No items present in the basket</Text>}
    </BasketItemsWrapper>);
}