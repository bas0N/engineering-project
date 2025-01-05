import { Text } from '@fluentui/react-components';
import { DeleteRegular, ChevronUpRegular, ChevronDownRegular } from '@fluentui/react-icons';
import { 
    FeaturesWrapper, 
    FeaturesInput, 
    FeaturesContainer,
    FeatureWrapper,
    FeatureText,
    FeatureOperations,
    FeatureOperationButton
} from "./Features.styled";
import { useTranslation } from 'react-i18next';
import { ChangeEvent, KeyboardEvent, useState } from 'react';

interface FeaturesProps {
    features: string[];
    setFeatures: (newFeatures: string[]) => void;
}

export const Features = ({
    features,
    setFeatures
}: FeaturesProps) => {
    const {t} = useTranslation();
    const [newFeature, setNewFeature] = useState('');

    const handleAddingNewFeature = () => {
        const operand = [...features, newFeature];
        setFeatures(operand)
        setNewFeature('');
    };

    const handleFeatureDeletion = (featureIndex: number) => {
        const operand = features.filter((_, ind) => ind !== featureIndex);
        setFeatures(operand);
    }

    const moveFeatureUp = (featureIndex: number) => {
        const upperContent = features[featureIndex-1];
        const operand = [...features];
        operand[featureIndex-1] = operand[featureIndex];
        operand[featureIndex] = upperContent;
        setFeatures(operand);
    };
    const moveFeatureDown = (featureIndex: number) => {
        const nextFeature = features[featureIndex+1];
        const operand = [...features];
        operand[featureIndex+1] = operand[featureIndex];
        operand[featureIndex] = nextFeature;
        setFeatures(operand);
    }

    return (<FeaturesWrapper>
        <Text as='h3'>{t('addProduct.features.header')}</Text>
        <FeaturesInput 
            appearance='underline'
            placeholder={t('addProduct.features.newFeaturePlaceholder')}
            value={newFeature}
            onKeyDown={(e: KeyboardEvent<HTMLInputElement>) => e.key === 'Enter' && handleAddingNewFeature()}
            onChange={(e: ChangeEvent<HTMLInputElement>) => setNewFeature(e.currentTarget.value)}
        />
        <FeaturesContainer>
            {
                features.map((feature, ind) => <FeatureWrapper key={`feature-${ind}`}>
                    <FeatureText>
                        {feature}
                    </FeatureText>
                    <FeatureOperations>
                        <FeatureOperationButton 
                            appearance='subtle'
                            disabled={ind === 0} 
                            onClick={() => moveFeatureUp(ind)}
                            aria-label={t('addProduct.features.moveUpLabel')}
                        >
                            <ChevronUpRegular />
                        </FeatureOperationButton>
                        <FeatureOperationButton 
                            appearance='subtle'
                            disabled={ind === features.length-1} 
                            onClick={() => moveFeatureDown(ind)}
                            aria-label={t('addProduct.features.moveDownLabel')}
                        >
                            <ChevronDownRegular />
                        </FeatureOperationButton>
                        <FeatureOperationButton 
                            appearance='subtle'
                            onClick={() => handleFeatureDeletion(ind)}
                            aria-label={t('addProduct.features.deleteLabel')}
                        >
                            <DeleteRegular />
                        </FeatureOperationButton>
                    </FeatureOperations>
                </FeatureWrapper>)
            }
        </FeaturesContainer>
    </FeaturesWrapper>);
}