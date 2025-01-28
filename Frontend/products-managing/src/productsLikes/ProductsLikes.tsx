import { useCallback, useEffect, useState } from 'react';
import axios from 'axios';
import {
    ProductsWrapper,
    ProductCard,
    ProductImage,
    ProductDetails,
    ProductTitle,
    ProductPrice,
    ProductRating,
    ProductHeader,
    ProductInfo
} from './ProductsLikes.styled';
import { Spinner, Toast, ToastTitle, Text, useToastController } from '@fluentui/react-components';
import { useTranslation } from 'react-i18next';

export interface ImageResponse {
    thumb: string;
    large: string;
    variant: string;
    hiRes: string;
}

export interface ProductResponse {
    title: string;
    price: string;
    ratingNumber: number;
    averageRating: number;
    images: ImageResponse[];
    isActive: boolean;
}

export const ProductsLikes = () => {
    const [products, setProducts] = useState<ProductResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const toasterId = import.meta.env.VITE_PREVIEW_MODE ? 'mainToaster' : 'localToaster';
    const token = localStorage.getItem('authToken');
    const {dispatchToast} = useToastController(toasterId);
    const {t} = useTranslation();

    const fetchLikedProducts = useCallback(async () => {
        try {
            const response = await axios.get(`${import.meta.env.VITE_API_URL}like/my`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            setProducts(response.data);
        } catch {
            dispatchToast(<Toast>
                <ToastTitle>
                    {t('productsLikes.loadingError')}
                </ToastTitle>
            </Toast>, {intent: 'error',position: 'top-end'})
        } finally {
            setLoading(false);
        }
    }, [dispatchToast, t, token]);

    useEffect(() => {
        fetchLikedProducts();
    }, [fetchLikedProducts]);

    if (loading) {
        return <Spinner label={t('productsLikes.loading')} />;
    }

    return (
        <>
            <ProductHeader>{t('productsLikes.header')}</ProductHeader>
            <ProductsWrapper>
                {products.length > 0 ? products.map((product, index) => (
                    <ProductCard key={index} isActive={product.isActive}>
                        <ProductImage
                            src={product.images[0]?.thumb}
                            alt={product.title}
                        />
                        <ProductDetails>
                            <ProductTitle>{product.title}</ProductTitle>
                            <ProductInfo>
                                <Text weight='semibold'>{t('productLikes.price')}:</Text> <ProductPrice>${product.price}</ProductPrice>
                            </ProductInfo>
                            <ProductInfo>
                                <Text weight='semibold'>{t('productsLikes.rating')}:</Text> <ProductRating>{product.averageRating} ★ ({product.ratingNumber} {t('productsLikes.reviews')})</ProductRating>
                            </ProductInfo>
                        </ProductDetails>
                    </ProductCard>
                )) : <Text align='center' weight='semibold'>
                    {t('productsLikes.noProducts')}
                </Text>}
            </ProductsWrapper>
        </>
    );
};

export default ProductsLikes;
