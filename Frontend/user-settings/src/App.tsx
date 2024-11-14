import { UserSettingsWrapper, UserSettingsHeader } from './App.styled'
import { useTranslation } from 'react-i18next';
import { useState } from 'react';
import { PersonalData } from './components/PersonalData/PersonalData';
import { Address, NewAddress, UserAddresses } from './components/UserAddresses/UserAddresses';

import './i18n/i18n.tsx';

export const App = () => {

  const {t} = useTranslation();
  const [recentId, setRecentId] = useState(0);

  const [personalData, setPersonalData] = useState<PersonalData>({
    firstName: '',
    lastName: '',
    email: '',
  });

  const [userAddresses, setUserAddresses] = useState<Address[]>([{
    id: 1,
    street: "123 Main St",
    city: "Anytown",
    state: "Anystate",
    postalCode: "12345",
    country: "USA"
  },
  {
    id: 2,
    street: "456 Another St",
    city: "Othertown",
    state: "Otherstate",
    postalCode: "67890",
    country: "USA"
  },
  {
    id: 3,
    street: "123 Main St",
    city: "Anytown",
    state: "Anystate",
    postalCode: "12345",
    country: "USA"
  },
  {
    id: 4,
    street: "456 Another St",
    city: "Othertown",
    state: "Otherstate",
    postalCode: "67890",
    country: "USA"
  },
  {
    id: 5,
    street: "123 Main St",
    city: "Anytown",
    state: "Anystate",
    postalCode: "12345",
    country: "USA"
  },
  {
    id: 6,
    street: "456 Another St",
    city: "Othertown",
    state: "Otherstate",
    postalCode: "67890",
    country: "USA"
  },]);

  const addNewAddress = (newAddress: NewAddress) => {
    const operand = [...userAddresses];
    operand.push({
      ...newAddress,
      id: recentId
    });
    setRecentId((id) => id+1);
    setUserAddresses(operand);
  }

  const deleteAddress = (addressId: number) => {
    const operand = userAddresses.filter((address) => address.id !== addressId);
    setUserAddresses(operand);
  }

  return (
    <UserSettingsWrapper>
      <UserSettingsHeader>{t('userSettings.header')}</UserSettingsHeader>
      <PersonalData 
        personalData={personalData} 
        changePersonalData={setPersonalData} 
      />
      <UserAddresses 
        addresses={userAddresses} 
        addNewAddress={addNewAddress}  
        deleteAddress={deleteAddress}
      />
    </UserSettingsWrapper>
  )
}

export default App;
