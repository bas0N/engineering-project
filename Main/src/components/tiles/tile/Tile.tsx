import { useTranslation } from "react-i18next";
import { ItemType } from "../../search/lastItems/LastItems";
import { TileContainer, TileImage } from "./Tile.styled";
import { RatingDisplay, Text } from '@fluentui/react-components'; 

export interface TileProps extends Pick<ItemType, 'id' | 'title' | 'images' | 'price' | 'averageRating' | 'ratingNumber'> {

}

export const Tile = ({
    id,
    title,
    images,
    price,
    averageRating,
    ratingNumber,
}:TileProps) => {

    const {t} = useTranslation();

    return (
        <TileContainer href={`/products/${id}`}>
            <TileImage src={images[0]?.large} alt={`${title}-image`} />
            <Text as='h3' size={500}>{title}</Text>
            {
                ratingNumber === null || averageRating === null ? 
                <Text>{t('product.noRatings')}</Text>
                : <RatingDisplay value={averageRating} count={ratingNumber} />
            }
            <Text>{price}</Text>
        </TileContainer>
    );
}