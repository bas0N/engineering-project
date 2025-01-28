import { useTranslation } from 'react-i18next';
import { useEffect, useState, useCallback } from "react";
import axios from "axios";
import { useParams } from "react-router-dom";
import { 
    ProductWrapper, 
    ProductPresentationSection,
    ProductDescriptionSection,
    ItemLoadingFailed,
} from "./Product.styled";
import { ImagesCarousel } from "../../components/product/ImagesCarousel/ImagesCarousel";
import { DetailsAndFeatures } from "../../components/product/DetailsAndFeatures/DetailsAndFeatures";
import { ProductPresentation } from "../../components/product/ProductPresentation/ProductPresentation";
import { ItemType } from "../../components/product/ProductPresentation/ProductPresentation";
import { Recommendations } from "../../components/product/Recommendations/Recommendations";
import { ReviewForm } from '../../components/product/ReviewForm/ReviewForm';
import { ReviewDisplay } from '../../components/product/ReviewDisplay/ReviewDisplay';
import '../../i18n/i18n';

const Product = () => {

    const params = useParams();
    const {t} = useTranslation();

    const [item, setItem] = useState<ItemType|null>(null);
    const [isReviewAdded, setIsReviewAdded] = useState(false);
    const [reviewDisplayReloadTriggerer, setReviewDisplayReloadTriggerer] = useState(false);
    const token = localStorage.getItem('authToken');

    const getItemData = useCallback(async() => {
        try {
            const result = await axios.get(`${import.meta.env.VITE_API_URL}product/${params.productId}`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            setItem(result.data);

        } catch (error) {
            console.log(error)
        }
    }, [params.productId, token]);

    useEffect(() => {
        getItemData();
    }, [getItemData]);

    //if(params.productId === undefined) return <></>

    const closeReviewForm = async() => {
        setIsReviewAdded(false);
        setReviewDisplayReloadTriggerer((state) => !state);
    }

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
                            setIsReviewAdded={setIsReviewAdded}
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
                    {
                        isReviewAdded && <ReviewForm 
                            productId={params.productId as string}
                            token={token as string}
                            closeReviewForm={closeReviewForm}
                        />
                    }
                    <ReviewDisplay
                        productId={params.productId as string}
                        token={token as string}
                        reloadTriggerer={reviewDisplayReloadTriggerer}
                    />
                    <Recommendations 
                        searchId={'B01GF7ERNC'}
                        type='product'
                    />
                    <Recommendations 
                        searchId={'AE25NQAZI3725GZIL5FS52ZIKWKQ'}
                        type='user'
                    />
                </ProductWrapper>
            }
        </>
    )
}

export default Product;