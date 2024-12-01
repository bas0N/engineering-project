import { useTranslation } from 'react-i18next';
import { 
    ProductWrapper, 
    ProductPresentationSection,
    ProductDescriptionSection,
    ItemLoadingFailed,
} from "./Product.styled";
import { ImagesCarousel } from "../../components/product/ImagesCarousel/ImagesCarousel";
import { DetailsAndFeatures } from "../../components/product/DetailsAndFeatures/DetailsAndFeatures";
import { ProductPresentation } from "../../components/product/ProductPresentation/ProductPresentation";
import { useEffect, useState } from "react";
import { ItemType } from "../../components/product/ProductPresentation/ProductPresentation";
import '../../i18n/i18n.tsx';
import { Recommendations } from "../../components/product/Recommendations/Recommendations.tsx";
import axios from "axios";
import { useParams } from "react-router-dom";

const Product = () => {

    const params = useParams();
    console.log(params);
    const {t} = useTranslation();

    const [item, setItem] = useState<ItemType|null>(null);
    const token = localStorage.getItem('authToken');

    useEffect(() => {
        const getItemData = async() => {
            try {
                const result = await axios.get(`${import.meta.env.VITE_API_URL}product/${params.productId}`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                console.log(result.data);
                setItem(result.data);

            } catch (error) {
                console.log(error)
            }
        };
        getItemData();
    }, [params, token]);

    //if(params.productId === undefined) return <></>

    return (
        <>
            {
                item === null ? <ItemLoadingFailed>
                    {t('product.loadingFailed')}
                </ItemLoadingFailed> : 
                <ProductWrapper>
                    <ProductPresentationSection height={80}>
                        <ImagesCarousel title={item.title} images={item.images} /> 
                        <ProductPresentation 
                            title={item.title}
                            categories={item.categories}
                            price={item.price}
                            ratingNumber={item.ratingNumber}
                            averageRating={item.averageRating}
                            productId={params.productId as string}
                            token={token as string}
                        />
                    </ProductPresentationSection>
                    <ProductDescriptionSection>
                        {item.description}
                    </ProductDescriptionSection>
                    <ProductPresentationSection height={50}>
                        <DetailsAndFeatures 
                            features={item.features} 
                            details={item.details} 
                        />
                    </ProductPresentationSection>

                    <Recommendations productId={params.productId as string} />
                </ProductWrapper>
            }
        </>
    )
}

export default Product;