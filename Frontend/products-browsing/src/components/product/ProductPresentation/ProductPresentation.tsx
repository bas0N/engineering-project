import { useTranslation } from "react-i18next";
import { useState } from "react";
import { Tag, Text } from "@fluentui/react-components";
import { ProductCategoriesWrapper } from "../DetailsAndFeatures/DetailsAndFeatures.styled";
import { 
    ProductPresentationOrderingSection, 
    ProductPresentationHeader, 
    ProductRatingDisplay, 
    ProductBuyingSection, 
    ProductPrice, 
    ProductAmountInput, 
    ProductAddToTheBaskedButton 
} from "./ProductPresentation.styled";
import { ProductImage } from '../ImagesCarousel/ImagesCarousel.tsx';

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

interface ProductPresentationProps extends Pick<ItemType, 'title' | 'categories' | 'ratingNumber' | 'averageRating' | 'price'>{}

export const ProductPresentation = ({
    title,
    categories,
    ratingNumber,
    averageRating,
    price
} : ProductPresentationProps) => {

    const [productNumber, setProductNumber] = useState(0);

    const {t} = useTranslation();

    return (
        <ProductPresentationOrderingSection>
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
                : <ProductRatingDisplay value={averageRating} count={ratingNumber} />
            }
            <ProductBuyingSection>
                <ProductPrice>{price}</ProductPrice>
                <ProductAmountInput 
                    aria-label={t('product.selectProductAmount')}
                    value={productNumber.toString()} 
                    onChange={(_e, data) => setProductNumber(Number(data.value))} 
                />
                <ProductAddToTheBaskedButton>
                    {t('product.addToBasket')}
                </ProductAddToTheBaskedButton>
            </ProductBuyingSection>
        </ProductPresentationOrderingSection>
    );
}