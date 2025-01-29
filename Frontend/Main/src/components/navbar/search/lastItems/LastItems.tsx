import { Search24Regular } from "@fluentui/react-icons";
import { 
    LastItemsWrapper, 
    LastItemsListContainer, 
    LastItemsRelatedContainer, 
    LastItemsListItem,
    LastItemsRelatedItemImage
} from "./LastItems.styled";
import { RatingDisplay, Text } from '@fluentui/react-components';
import { useState } from "react";
import { useNavigate } from "react-router";

export type ProductImage = {
    thumb: string,
    large: string,
    variant: string,
    hiRes: string | null,
};

export type ItemType = {
    id: string;
    boughtTogether: string | null;
    categories: string[];
    description: string[];
    details: Record<string,string>;
    features: string[];
    images: ProductImage[],
    averageRating: number | null,
    mainCategory: string | null,
    parentAsin: string,
    price: string,
    ratingNumber: number | null,
    store: string,
    title: string,
    videos: {
        title: string,
        url: string,
        userId: string | null,
    }[],
}

export interface LastItemsProps {
    items: ItemType[];
    closeLastItems: () => void;
}

export const LastItems = ({items, closeLastItems}: LastItemsProps) => {

    const [hoveredItem, setHoveredItem] = useState<ItemType | null>(null);
    const navigate = useNavigate();

    const navigateToProduct = (productId: string) => {
        navigate(`/products/${productId}`);
        closeLastItems();
    }

    const onRelatedItemClick = () => {
        if(hoveredItem !== null){
            navigateToProduct(hoveredItem.parentAsin);
            closeLastItems();
        }
    };

    return (<LastItemsWrapper onMouseLeave={() => setHoveredItem(null)}>
           <LastItemsListContainer>
                {items.map((item, ind) => (
                    <LastItemsListItem 
                        key={`items-searched-${item.parentAsin}`}
                        bgOpacity={ind % 2 === 0 ? '88' : '78'}
                        onMouseEnter={() => setHoveredItem(item)}
                        onClick={() => navigateToProduct(item.parentAsin)}
                    >
                        <Search24Regular />
                        <Text size={400}>{item.title.length > 70 ? item.title.substring(0, 67)+'...' : item.title}</Text>
                    </LastItemsListItem>
                ))}
           </LastItemsListContainer>
           {
                hoveredItem !== null && (
                    <LastItemsRelatedContainer onClick={onRelatedItemClick}>
                        <LastItemsRelatedItemImage src={hoveredItem.images[0]?.large ?? ''} alt={hoveredItem.title} /> 
                        <Text 
                            as='h3'
                            align="center" 
                            size={400}
                            weight="semibold"
                        >
                            {hoveredItem.title}
                        </Text>
                        {
                            hoveredItem.ratingNumber === null || hoveredItem.averageRating === null ? <Text>No ratings yet</Text>
                            : <RatingDisplay 
                            value={hoveredItem.averageRating} 
                            count={hoveredItem.ratingNumber}
                        />
                        }
                    </LastItemsRelatedContainer>
                )
            }
        </LastItemsWrapper>
    );
};