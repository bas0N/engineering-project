import { Input, Text } from "@fluentui/react-components";
import { PersonalDataWrapper } from "./PersonalData.styled";
import { useTranslation } from "react-i18next";

export type PersonalData = {
    firstName: string;
    lastName: string;
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

    const handleChangeOfData = (newData: string, property: keyof PersonalData) => {
        const newPersonalData = {
            ...personalData,
            [property]: newData
        };
        changePersonalData(newPersonalData);
    };

    return (
        <>
            <Text as='h2' size={600}>{t('userSettings.personalDataHeader')}</Text>
            <PersonalDataWrapper>
                <Input type='text' 
                    aria-label={t('userSettings.firstName')} 
                    placeholder={t('userSettings.firstName')} 
                    value={personalData.firstName}
                    onChange={(e) => handleChangeOfData(e.currentTarget.value, 'firstName')}
                />
                <Input type='text' 
                    aria-label={t('userSettings.lastName')} 
                    placeholder={t('userSettings.lastName')} 
                    value={personalData.lastName}
                    onChange={(e) => handleChangeOfData(e.currentTarget.value, 'lastName')}
                />
                <Input type='text' 
                    aria-label={t('userSettings.email')} 
                    placeholder={t('userSettings.email')} 
                    value={personalData.email}
                    onChange={(e) => handleChangeOfData(e.currentTarget.value, 'email')}
                />
            </PersonalDataWrapper>
        </>
    );
}