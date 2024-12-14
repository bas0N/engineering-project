import { useState } from 'react';
import { useTranslation } from "react-i18next";
import { Button, Text } from "@fluentui/react-components";
import { PersonalDataWrapper, PersonalDataInputs, PersonalDataInput } from "./PersonalData.styled";

export type PersonalData = {
    firstName: string;
    lastName: string;
    phoneNumber: string
    email: string;
}; 

interface PersonalDataProps {
    personalData: PersonalData;
    changePersonalData: (newPersonalData: PersonalData) => void;
}

export const PersonalData = ({
    personalData,
    changePersonalData,
} : PersonalDataProps) => {

    const {t} = useTranslation();

    const [localPersonalData, setLocalPersonalData] = useState<PersonalData>({...personalData});

    const handleChangeOfData = (newData: string, property: keyof PersonalData) => {
        const newPersonalData = {
            ...localPersonalData,
            [property]: newData
        };
        setLocalPersonalData(newPersonalData);
    };

    const submitChanges = () => {
        changePersonalData(localPersonalData);
    }

    return (
        <>
            <Text as='h2' size={600}>{t('userSettings.personalDataHeader')}</Text>
            <Text align='center'>{t('userSettings.emailLabel')}{personalData.email}</Text>
            <PersonalDataWrapper>
                <PersonalDataInputs>
                    <PersonalDataInput type='text' 
                        aria-label={t('userSettings.firstName')} 
                        placeholder={t('userSettings.firstName')} 
                        value={localPersonalData.firstName}
                        onChange={(e) => handleChangeOfData(e.currentTarget.value, 'firstName')}
                    />
                    <PersonalDataInput type='text' 
                        aria-label={t('userSettings.lastName')} 
                        placeholder={t('userSettings.lastName')} 
                        value={localPersonalData.lastName}
                        onChange={(e) => handleChangeOfData(e.currentTarget.value, 'lastName')}
                    />
                    <PersonalDataInput type='text' 
                        aria-label={t('userSettings.phoneNumber')} 
                        placeholder={t('userSettings.phoneNumber')} 
                        value={localPersonalData.phoneNumber}
                        onChange={(e) => handleChangeOfData(e.currentTarget.value, 'phoneNumber')}
                    />
                </PersonalDataInputs>
                <Button onClick={submitChanges}>
                    {t('userSettings.submitChangesToPersonalData')}
                </Button>
            </PersonalDataWrapper>
        </>
    );
}