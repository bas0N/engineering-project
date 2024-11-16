import { useTranslation } from "react-i18next";

import { TileContainer, TileImage } from "./Tile.styled";
import { RatingDisplay, Text } from '@fluentui/react-components'; 
import { ItemType } from "../../product/ProductPresentation/ProductPresentation";

export interface TileProps extends Pick<ItemType, 'id' | 'title' | 'images' | 'price' | 'averageRating' | 'ratingNumber'> {
    height?: number;
}

export const Tile = ({
    id,
    title,
    images,
    price,
    averageRating,
    ratingNumber,
    height,
}:TileProps) => {

    const {t} = useTranslation();

    return (
        <TileContainer href={`/products/${id}`} height={height}>
            <TileImage src={images[0]?.large} alt={`${title}-image`} />
            <Text as='h3' size={500} align='center'>{title}</Text>
            {
                ratingNumber === null || averageRating === null ? 
                <Text>{t('product.noRatings')}</Text>
                : <RatingDisplay value={averageRating} count={ratingNumber} />
            }
            <Text>{price}</Text>
        </TileContainer>
    );
}