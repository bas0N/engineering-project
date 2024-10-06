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

export type ItemType = {
    title: string;
    image: string;
    _id: string;
    average_rating: number;
    rating_number: number;
    price: string;
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
            navigateToProduct(hoveredItem._id);
        }
    };

    return (<LastItemsWrapper onMouseLeave={() => setHoveredItem(null)}>
           <LastItemsListContainer>
                {items.map((item, ind) => (
                    <LastItemsListItem 
                        key={`items-searched-${item._id}`}
                        bgOpacity={ind % 2 === 0 ? '58' : '38'}
                        onMouseEnter={() => setHoveredItem(item)}
                        onClick={() => navigateToProduct(item._id)}
                    >
                        <Search24Regular />
                        <Text size={400}>{item.title.length > 70 ? item.title.substring(0, 67)+'...' : item.title}</Text>
                    </LastItemsListItem>
                ))}
           </LastItemsListContainer>
           {
                hoveredItem !== null && (
                    <LastItemsRelatedContainer onClick={onRelatedItemClick}>
                        <LastItemsRelatedItemImage src={hoveredItem.image} alt={hoveredItem.title} /> 
                        <Text 
                            as='h3'
                            align="center" 
                            size={400}
                            weight="semibold"
                        >
                            {hoveredItem.title}
                        </Text>
                        {
                            Number(hoveredItem.rating_number) === 0 ? <Text>No ratings yet</Text>
                            : <RatingDisplay 
                            value={hoveredItem.average_rating} 
                            count={hoveredItem.rating_number}
                        />
                        }
                    </LastItemsRelatedContainer>
                )
            }
        </LastItemsWrapper>
    );
};