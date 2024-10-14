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
import { ProductImage } from '../../product/ImagesCarousel';

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
    parentAsin: string | null,
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

interface LastItemsProps {
    items: ItemType[];
}
export const LastItems = ({items}: LastItemsProps) => {

    const [hoveredItem, setHoveredItem] = useState<ItemType | null>(null);
    const navigate = useNavigate();

    const navigateToProduct = (productId: string) => navigate(`/products/${productId}`);

    const onRelatedItemClick = () => {
        if(hoveredItem !== null){
            navigateToProduct(hoveredItem.id);
        }
    };

    return (<LastItemsWrapper onMouseLeave={() => setHoveredItem(null)}>
           <LastItemsListContainer>
                {items.map((item, ind) => (
                    <LastItemsListItem 
                        key={`items-searched-${item.id}`}
                        bgOpacity={ind % 2 === 0 ? '58' : '38'}
                        onMouseEnter={() => setHoveredItem(item)}
                        onClick={() => navigateToProduct(item.id)}
                    >
                        <Search24Regular />
                        <Text size={400}>{item.title.length > 70 ? item.title.substring(0, 67)+'...' : item.title}</Text>
                    </LastItemsListItem>
                ))}
           </LastItemsListContainer>
           {
                hoveredItem !== null && (
                    <LastItemsRelatedContainer onClick={onRelatedItemClick}>
                        <LastItemsRelatedItemImage src={hoveredItem.images[0].large} alt={hoveredItem.title} /> 
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