import { Text } from "@fluentui/react-components"
import { 
    RecommendationsContainer, 
    RecommendationsWrapper, 
    RecommendationTile, 
} from "./Recommendations.styled"
import { useTranslation } from "react-i18next"
import { useEffect, useState } from "react";

import { ItemType } from "../ProductPresentation/ProductPresentation";
import { Tile } from "../../tiles/tile/Tile";
import axios from "axios";

interface RecommendationsProps {
    searchId: string;
    type: 'product' | 'user'
}

type ItemTypeRecommendations = {} & Pick<ItemType, 'id' | 'price' | 'title' | 'images' | 'ratingNumber' | 'averageRating'>;

type RawData = {
    ids: string,
    rating_number: number,
    average_rating: number,
    title: string,
    image: string,
    price: number
}

export const Recommendations = ({
    searchId,
    type
}: RecommendationsProps) => {

    const {t} = useTranslation();
    const [products, setProducts] = useState<ItemTypeRecommendations[]|null>(null);
    const token = localStorage.getItem('authToken');

    useEffect(() => {
        const getData = async() => {
            const recomSystem = type === 'product' ? import.meta.env.VITE_API_RECOMMENDATIONS : import.meta.env.VITE_API_RECOMMENDATIONS_PEOPLE;
            const system = type === 'product' ? 1 : 2;
            const param = type === 'product' ? 'product_id' : 'user_id';
            const result = await axios.get(`${recomSystem}recc-system-${system}?${param}=${searchId}&number_of_products=4`,{
                headers:{
                    'Authorization': `Bearer ${token}`
                }
            });
            
            const recomsRaw:RawData[] = result.data;
            recomsRaw.shift();
            const data = recomsRaw.map((elem: RawData) => ({
                ...elem,
                id: elem.ids,
                title: elem.title,
                price: String(elem.price),
                images: [{
                    thumb: elem.image,
                    large: elem.image,
                    hiRes: elem.image,
                    variant: elem.image,
                }],
                averageRating: elem.average_rating,
                ratingNumber: elem.rating_number
            }) as ItemTypeRecommendations);
            setProducts(data);
        };
        getData();
    }, [searchId, token, type]);

    return (
        <RecommendationsContainer>
            <Text as='h4' size={600}>
                {type === 'product' ? t('product.recommendations') : t('product.recommendationsUser')}
            </Text>
            <RecommendationsWrapper>
                {products === null ? 
                    <Text as='h4' size={400}>{t('product.recommendationsFailure')}</Text>
                : products.length === 0 ? (
                    <Text as='h4' size={400}>{t('product.noRecommendations')}</Text>
                ) : products?.map((product, ind) => <RecommendationTile>
                    <Tile 
                        height={60}
                        key={`product-${product.id}-${ind}`}
                        id={product.id}
                        title={product.title}
                        images={product.images}
                        averageRating={product.averageRating}
                        ratingNumber={product.ratingNumber}
                        price={product.price}
                    />
                    </RecommendationTile> )}
            </RecommendationsWrapper>
        </RecommendationsContainer>
    )
}