import { Button, Divider, Input, Text } from '@fluentui/react-components';
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

interface DetailsProps {
    details: Record<string,string>;
    setDetails: (newDetails: Record<string,string>) => void;
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
            operand[newDetailName] = newDetailValue;
            setDetails(operand);
            setNewDetailName('');
            setNewDetailValue('');
        }
    }

    const handleDetailDeletion = (detailName: string) => {
        const operand = {...details};
        delete operand[detailName];
        setDetails(operand);
    };

    const handleEditingDetail = (detailName: string) => {
        const operand = {...details};
        const detailValue = operand[detailName];
        delete operand[detailName];
        setNewDetailName(detailName);
        setNewDetailValue(detailValue);
        setDetails(operand);
    }

    return (<DetailsWrapper>
        <Text as='h3'>{t('addProduct.details.header')}</Text>
        <DetailsAddingContainer>
            <Input 
                value={newDetailName}
                onChange={(e: ChangeEvent<HTMLInputElement>) => setNewDetailName(e.currentTarget.value)}
                placeholder={t('addProduct.details.detailNamePlaceholder')} 
            />
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
                Object.keys(details).map((detailName) => (<>
                    <DetailsRow>
                        <DetailsRowText>
                            {detailName}
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