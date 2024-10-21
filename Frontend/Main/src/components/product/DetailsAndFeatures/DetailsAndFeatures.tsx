import { useTranslation } from '../../../../node_modules/react-i18next';
import { Text } from '@fluentui/react-components';
import { ProductDetailsAndFeatures, ProductsDetailsAndFeaturesHeader, ProductsDetailWrapper, ProductsDetailsAndFeaturesListItem } from "./DetailsAndFeatures.styled";

interface DetailsAndFeaturesProps {
    details: Record<string, string>;
    features: string[];
}

export const DetailsAndFeatures = ({
    details, features,
} : DetailsAndFeaturesProps) => {
    
    const {t} = useTranslation();
    
    return (<>
        <ProductDetailsAndFeatures>
            <ProductsDetailsAndFeaturesHeader>
                {t('product.detailsHeader')}
            </ProductsDetailsAndFeaturesHeader>
            {Object.keys(details).map((detail) => (
                <ProductsDetailWrapper key={`product-detail-${detail}`}>
                    <Text weight="semibold" size={400}>{detail}</Text>
                    <Text size={400}>{details[detail]}</Text>
                </ProductsDetailWrapper>
            ))}
        </ProductDetailsAndFeatures>
        <ProductDetailsAndFeatures gapSize={8}>
            <ProductsDetailsAndFeaturesHeader>
                {t('product.featuresHeader')}
            </ProductsDetailsAndFeaturesHeader>
            {features.map((feature) => (
                <ProductsDetailsAndFeaturesListItem key={`product-feature-${feature}`}>
                    {feature}
                </ProductsDetailsAndFeaturesListItem>
            ))}
        </ProductDetailsAndFeatures>
    </>);
}