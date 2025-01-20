import { 
    Button, 
    Divider, 
    Dropdown, 
    DropdownProps,
    Input, 
    Option,
    Text,
} from '@fluentui/react-components';
import { DeleteRegular, EditRegular } from '@fluentui/react-icons';
import { useTranslation } from "react-i18next";
import { 
    DetailsContainer,
    DetailsWrapper,
    DetailsAddingContainer, 
    DetailsRowText,
    DetailsRow,
    DetailsDeletionButton,
    DetailsOperations,
} from './Details.styled';
import { ChangeEvent, useState } from 'react';

export interface DetailsProps {
    details: Record<string,string>;
    setDetails: (newDetails: Record<string,string>) => void;
}

const productDetails: string[] = [
    "MATERIAL_FEATURE",
    "PRODUCT_DIMENSIONS",
    "ITEM_FORM",
    "HEAD_TYPE",
    "PACKAGE_INFORMATION",
    "DEPARTMENT",
    "IS_DISCONTINUED_BY_MANUFACTURER",
    "MANUFACTURER",
    "BATTERIES",
    "BRAND",
    "ACTIVE_INGREDIENTS",
    "DATE_FIRST_AVAILABLE",
    "NUMBER_OF_ITEMS",
    "ITEM_WEIGHT",
    "DOSAGE_FORM",
    "PACKAGE_DIMENSIONS",
    "BLADE_MATERIAL",
    "NUMBER_OF_BLADES",
    "PRODUCT_BENEFITS",
    "ITEM_MODEL_NUMBER",
    "UNIT_COUNT",
    "AGE_RANGE",
    "MATERIAL",
    "FLAVOR"
];

export const processDetailName = (detail: string): string => {
    return detail
        .toLowerCase()
        .split('_')
        .map((word, index) => 
            index === 0 ? word.charAt(0).toUpperCase() + word.slice(1) : word
        )
        .join(' ');
};

export const processDetailNameForAdding = (detail: string): string => {
    return detail.split(' ').map((word) => word.toUpperCase()).join('_');
}

export const Details = ({
    details,
    setDetails
}:DetailsProps) => {
    const {t} = useTranslation();
    const [newDetailName, setNewDetailName] = useState('');
    const [newDetailValue, setNewDetailValue] = useState('');

    const handleAddingDetail = () => {
        const operand = {...details};
        if(newDetailName.length > 0 && newDetailValue.length > 0){
            operand[processDetailNameForAdding(newDetailName)] = newDetailValue;
            setDetails(operand);
            setNewDetailName('');
            setNewDetailValue('');
        }
    }

    const handleDetailDeletion = (detailName: string) => {
        const operand = {...details};
        delete operand[processDetailNameForAdding(detailName)];
        setDetails(operand);
    };

    const handleEditingDetail = (detailName: string) => {
        const operand = {...details};
        const processedDetailName = processDetailNameForAdding(detailName);
        const detailValue = operand[processedDetailName];
        delete operand[processedDetailName];
        setNewDetailName(detailName);
        setNewDetailValue(detailValue);
        setDetails(operand);
    }

    const onDetailSelect: DropdownProps['onOptionSelect'] = (_ev, data) => {
        setNewDetailName(data.optionText as string);
    };

    const selectedDetailsKeys = Object.keys(details);
    const filteredProductDetails = productDetails.filter((detail) => !selectedDetailsKeys.find((selected) => selected === detail)); 

    return (<DetailsWrapper>
        <Text as='h3'>{t('addProduct.details.header')}</Text>
        <DetailsAddingContainer>
            <Dropdown
                aria-label={t('addProduct.details.detailNamePlaceholder')}
                value={processDetailName(newDetailName)}
                selectedOptions={[processDetailName(newDetailName)]}
                onOptionSelect={onDetailSelect}
            >
                {
                    filteredProductDetails.map((detail) => (<Option
                        value={processDetailName(detail)}
                        text={processDetailName(detail)}
                    >
                        <Text>
                            {processDetailName(detail)}
                        </Text>
                    </Option>))
                }
            </Dropdown>
            <Input 
                value={newDetailValue}
                onChange={(e: ChangeEvent<HTMLInputElement>) => setNewDetailValue(e.currentTarget.value)}
                placeholder={t('addProduct.details.detailValuePlaceholder')} 
            />
            <Button 
                aria-label={t('addProduct.details.addDetailLabel')}
                onClick={() => handleAddingDetail()}
            >
                {t('addProduct.details.addDetailLabel')}
            </Button>
        </DetailsAddingContainer>
        <DetailsContainer>
            {
                selectedDetailsKeys.map((detailName) => (<>
                    <DetailsRow>
                        <DetailsRowText>
                            {processDetailName(detailName)}
                        </DetailsRowText>
                        <DetailsRowText>
                            {details[detailName]}
                        </DetailsRowText>
                        <DetailsOperations>
                            <DetailsDeletionButton 
                                aria-label={t('addProduct.details.editLabel')}
                                onClick={() => handleEditingDetail(detailName)}
                            >
                                <EditRegular />
                            </DetailsDeletionButton>
                            <DetailsDeletionButton 
                                aria-label={t('addProduct.details.deleteLabel')}
                                onClick={() => handleDetailDeletion(detailName)}
                            >
                                <DeleteRegular />
                            </DetailsDeletionButton>
                        </DetailsOperations>
                    </DetailsRow>
                    <Divider />
                </>))
            }
        </DetailsContainer>
    </DetailsWrapper>)
}