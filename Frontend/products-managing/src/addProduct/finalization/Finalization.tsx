import { Tag, Text } from '@fluentui/react-components';
import { useTranslation } from "react-i18next";

import { 
    FinalizationRow, 
    FinalizationTitle,
    FinalizationContent,
    FinalizationValue,
    FinalizationContentRow,
    FinalizationContentText,
} from './Finalization.styled';
import { processDetailName } from '../details/Details';

interface FinalizationProps {
    title: string;
    details: Record<string,string>;
    description: string;
    features: string[];
    categories: string[];
}

export const Finalization = ({
    title,
    details,
    description,
    features,
    categories
} : FinalizationProps) => {
    const {t} = useTranslation();

    return (<>
        <FinalizationRow>
            <FinalizationTitle>
                {t('addProduct.finalization.title')}
            </FinalizationTitle>
            <FinalizationValue align='center'>
                {title}
            </FinalizationValue>
        </FinalizationRow>
        <FinalizationRow>
            <FinalizationTitle>
                {t('addProduct.finalization.description')}
            </FinalizationTitle>
            <FinalizationValue align='center'>
                {description}
            </FinalizationValue>
        </FinalizationRow>
        <FinalizationRow>
            <FinalizationTitle>
                {t('addProduct.finalization.categories')}
            </FinalizationTitle>
            <FinalizationContent flexDirection='row'>
                {categories.map((category) => (<Tag appearance='brand' key={`category-${category}`}>
                    {category}
                </Tag>))}
            </FinalizationContent>
        </FinalizationRow>
        <FinalizationRow>
            <FinalizationTitle>
                {t('addProduct.finalization.details')}
            </FinalizationTitle>
            <FinalizationContent flexDirection='column'>
                {Object.keys(details).map((detail) => (<FinalizationContentRow key={`detail-${detail}`}>
                    <Text>
                        {processDetailName(detail)}
                    </Text>
                    <Text>
                        {details[detail]}
                    </Text>
                </FinalizationContentRow>))}
            </FinalizationContent>
        </FinalizationRow>
        <FinalizationRow>
            <FinalizationTitle>
                {t('addProduct.finalization.features')}
            </FinalizationTitle>
            <FinalizationContent flexDirection='row'>
                {features.map((feature) => (<FinalizationContentText>{feature}</FinalizationContentText>))}
            </FinalizationContent>
        </FinalizationRow>
    </>)
};