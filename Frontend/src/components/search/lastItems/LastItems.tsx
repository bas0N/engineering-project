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
    name: string;
    image: string;
    id: string;
    ratings: string;
    no_of_ratings: string | null;
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

    const convertRatingsToNumber = (ratings: string) => Number(ratings.split(',').join(''));

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
                        <Text size={400}>{item.name.length > 70 ? item.name.substring(0, 67)+'...' : item.name}</Text>
                    </LastItemsListItem>
                ))}
           </LastItemsListContainer>
           {
                hoveredItem !== null && (
                    <LastItemsRelatedContainer onClick={onRelatedItemClick}>
                        <LastItemsRelatedItemImage src={hoveredItem.image} alt={hoveredItem.name} /> 
                        <Text 
                            as='h3'
                            align="center" 
                            size={400}
                            weight="semibold"
                        >
                            {hoveredItem.name}
                        </Text>
                        {
                            Number(hoveredItem.ratings) === 0 ? <Text>No ratings yet</Text>
                            : <RatingDisplay 
                            value={Number(hoveredItem.ratings)} 
                            count={hoveredItem.no_of_ratings !== null ? convertRatingsToNumber(hoveredItem.no_of_ratings) : undefined}
                        />
                        }
                    </LastItemsRelatedContainer>
                )
            }
        </LastItemsWrapper>
    );
};