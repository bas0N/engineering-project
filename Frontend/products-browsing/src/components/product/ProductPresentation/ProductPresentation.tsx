import { useTranslation } from "react-i18next";
import { useEffect, useState, useCallback } from "react";
import { Button, useId, Tag, Text, Toast, Toaster, ToastTitle, useToastController } from "@fluentui/react-components";
import { ThumbLikeRegular } from '@fluentui/react-icons';
import { ProductCategoriesWrapper } from "../DetailsAndFeatures/DetailsAndFeatures.styled";
import { 
    ProductPresentationOrderingSection, 
    ProductPresentationHeader, 
    ProductRatingDisplay, 
    ProductBuyingSection, 
    ProductPrice, 
    ProductAmountInput, 
    ProductAddToTheBaskedButton, 
    ProductLikeingSection
} from "./ProductPresentation.styled";
import { ProductImage } from '../ImagesCarousel/ImagesCarousel.tsx';
import axios from "axios";

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

interface ProductPresentationProps extends Pick<ItemType, 'title' | 'categories' | 'ratingNumber' | 'averageRating' | 'price'>{
    productId: string;
    token: string;
    setIsReviewAdded: (newState: boolean) => void;
}

export const ProductPresentation = ({
    title,
    categories,
    ratingNumber,
    averageRating,
    price,
    productId,
    token,
    setIsReviewAdded
} : ProductPresentationProps) => {

    const toasterId = useId("toaster");
    const [productNumber, setProductNumber] = useState(0);
    const [numberOfLikes, setNumberOfLikes] = useState(0);
    const [isLiked, setIsLiked] = useState(false);
    const { dispatchToast } = useToastController(toasterId);

    const {t} = useTranslation();

    const getLikesNumberInfo = useCallback(async() => {

        const result = await axios.get(`${import.meta.env.VITE_API_URL}like/number/${productId}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        setNumberOfLikes(result.data);
    }, [productId, token]);

    useEffect(() => {
        const getLikesInfo = async() => {
            try {
                await getLikesNumberInfo();
                const isLikedResult = await axios.get(`${import.meta.env.VITE_API_URL}like/isLiked/${productId}`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                setIsLiked(isLikedResult.data);
            } catch (error) {
                console.log(error);
            }
        }
        getLikesInfo();
    }, [productId, token, getLikesNumberInfo]);

    const likeProduct = async() => {
        try {
            if(isLiked){
                await axios.delete(`${import.meta.env.VITE_API_URL}like/remove/${productId}`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
            } else {
                await axios.post(`${import.meta.env.VITE_API_URL}like/${productId}`, {}, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
            }
            setIsLiked((liked) => !liked);
            await getLikesNumberInfo();
        } catch (error) { 
            console.log(error);
        }
    }

    const addToBasket = async() => {
        try {
            const result = await axios.post(`${import.meta.env.VITE_API_URL}basket`, {
                quantity: productNumber,
                product: productId
            }, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            console.log(result);
            dispatchToast(<Toast>
                    <ToastTitle>{t('product.addedToBasket')}</ToastTitle>
                </Toast>, 
                {position: 'top-end', intent: 'success'}
            );
        } catch {
            dispatchToast(<Toast>
                    <ToastTitle>{t('product.failedToAdd')}</ToastTitle>
                </Toast>, 
                {position: 'top-end', intent: 'error'}
            );
        }
    }

    return (
        <ProductPresentationOrderingSection>
            <Toaster toasterId={toasterId} />
            <ProductPresentationHeader>
                {title}
            </ProductPresentationHeader>
            <ProductCategoriesWrapper>
                {categories.map((category) => (
                    <Tag appearance="brand" key={`product-category-${category}`}>
                        {category}
                    </Tag>
                ))}
            </ProductCategoriesWrapper>
            {
                ratingNumber === null || averageRating === null ? <Text>{t('product.noRatings')}</Text>
                : <>
                    <ProductRatingDisplay value={averageRating} count={ratingNumber} />
                    <ProductLikeingSection>
                        <Button 
                            appearance={isLiked ? 'primary' : 'subtle'} 
                            icon={<ThumbLikeRegular />} 
                            onClick={likeProduct}
                        />
                        <Text>{numberOfLikes}</Text>
                    </ProductLikeingSection>
                    <Button appearance="subtle" onClick={() => setIsReviewAdded(true)}>
                        {t('product.addReview')}
                    </Button>
                </>
            }
            <ProductBuyingSection>
                <ProductPrice>{price}</ProductPrice>
                <ProductAmountInput 
                    aria-label={t('product.selectProductAmount')}
                    value={productNumber.toString()} 
                    onChange={(_e, data) => setProductNumber(Number(data.value))} 
                />
                <ProductAddToTheBaskedButton 
                    disabled={productNumber === 0}
                    onClick={() => addToBasket()}
                >
                    {t('product.addToBasket')}
                </ProductAddToTheBaskedButton>
            </ProductBuyingSection>
        </ProductPresentationOrderingSection>
    );
}