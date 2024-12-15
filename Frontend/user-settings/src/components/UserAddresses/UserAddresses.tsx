import { useTranslation } from 'react-i18next';
import { useState } from 'react';
import { Button, Input, Text } from '@fluentui/react-components';
import { AddressContent, AddressOptions,
    AddressesWrapper, 
    UserAddressesAddingWrapper, 
    AddressContainer,
    NoAddresses
} from './UserAddresses.styled';
import { DeleteRegular } from '@fluentui/react-icons';

export type Address = {
    street: string;
    city: string;
    postalCode: string;
    state: string;
    country: string;
    id: number;
}

export type NewAddress = Omit<Address, 'id'>;

interface UserAddressesProps {
    addresses: Address[];
    addNewAddress: (newAddress: NewAddress) => void;
    deleteAddress: (addressId: number) => void;
}

export const UserAddresses = ({
    addresses,
    addNewAddress,
    deleteAddress,
} : UserAddressesProps) => {
    
    const {t} = useTranslation();
    const [isAddingOpened, setIsAddingOpened] = useState(false);

    const [newAddressData, setNewAddressData] = useState<NewAddress>({
        street: '',
        city: '',
        postalCode: '',
        state: '',
        country: '',
    });

    const onAddingNewAddress = () => {
        if(
            newAddressData.street.length === 0
            || newAddressData.city.length === 0
            || newAddressData.postalCode.length === 0
            || newAddressData.state.length === 0
            || newAddressData.country.length === 0
        ) {
            return;
        }
        addNewAddress(newAddressData);
        setNewAddressData({
            street: '',
            city: '',
            postalCode: '',
            state: '',
            country: '',
        });
        setIsAddingOpened(true);
    };

    const handleNewAddressChange = (newValue: string, addressProperty: keyof NewAddress) => {
        setNewAddressData((data) => ({
            ...data,
            [addressProperty]: newValue
        }));
    }

    return (
        <>
            <Text as='h2' size={600}>{t('userSettings.userAddressesHeader')}</Text>
            {isAddingOpened 
                ? (
                <>
                    <UserAddressesAddingWrapper>
                        <Input 
                            type='text' 
                            placeholder={t('userSettings.newAddress.streetInput')} 
                            value={newAddressData.street}
                            onChange={(e) => handleNewAddressChange(e.currentTarget.value, 'street')}
                        />
                        <Input 
                            type='text' 
                            placeholder={t('userSettings.newAddress.cityInput')} 
                            value={newAddressData.city}
                            onChange={(e) => handleNewAddressChange(e.currentTarget.value, 'city')}
                        />
                        <Input 
                            type='text' 
                            placeholder={t('userSettings.newAddress.stateInput')} 
                            value={newAddressData.state}
                            onChange={(e) => handleNewAddressChange(e.currentTarget.value, 'state')}
                        />
                        <Input 
                            type='text' 
                            placeholder={t('userSettings.newAddress.postalCodeInput')} 
                            value={newAddressData.postalCode}
                            onChange={(e) => handleNewAddressChange(e.currentTarget.value, 'postalCode')}
                        />
                        <Input 
                            type='text' 
                            placeholder={t('userSettings.newAddress.countryInput')} 
                            value={newAddressData.country}
                            onChange={(e) => handleNewAddressChange(e.currentTarget.value, 'country')}
                        />
                    </UserAddressesAddingWrapper>
                    <Button onClick={onAddingNewAddress}>
                        {t('userSettings.newAddress.submitAddingNewAddress')}
                    </Button>
                </>) : (<Button onClick={() => setIsAddingOpened(true)}>
                    {t('userSettings.addNewAddress')}
                </Button>)
            }
            {
                addresses.length === 0 ? (
                    <NoAddresses>{t('userSettings.noAddressesPresent')}</NoAddresses>
                ) : (
                    <AddressesWrapper>
                        {
                            addresses.map((address, ind) => <AddressContainer key={`address-${ind}`}>
                                <AddressContent>
                                    <Text as='h3'>{address.street}, {address.city}, {address.state}</Text>
                                    <Text as='h3'>{address.postalCode}, {address.country}</Text>
                                </AddressContent>
                                <AddressOptions>
                                    <Button 
                                        appearance='transparent'
                                        onClick={() => deleteAddress(address.id)}
                                        data-testid='deleteButton'
                                    >
                                        <DeleteRegular />
                                    </Button>
                                </AddressOptions>
                            </AddressContainer>)
                        }
                    </AddressesWrapper>
                )
            }
        </>
    )
}