import { UserSettingsWrapper, UserSettingsHeader } from './App.styled'
import { useTranslation } from 'react-i18next';
import { useEffect, useState } from 'react';
import axios from 'axios';
import { Spinner, Text, Toast, ToastTitle, useToastController } from '@fluentui/react-components';
import { PersonalData } from './components/PersonalData/PersonalData';
import { Address, NewAddress, UserAddresses } from './components/UserAddresses/UserAddresses';

import './i18n/i18n';

export const App = () => {

  const {t} = useTranslation();
  const token = localStorage.getItem('authToken');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const toasterId = import.meta.env.VITE_PREVIEW_MODE ? 'localToaster' : 'mainToaster';
  const {dispatchToast} = useToastController(toasterId);

  const [personalData, setPersonalData] = useState<PersonalData>({
    firstName: '',
    lastName: '',
    phoneNumber: '',
    email: '',
  });

  const [userAddresses, setUserAddresses] = useState<Address[]>([]);

  const addNewAddress = async (newAddress: NewAddress) => {
    const operand = [...userAddresses].map((elem) => ({
      ...elem,
      operation: "UPDATE"
    }));
    operand.push({
      ...newAddress,
      operation: "CREATE",
      id: 123
    });
    try {
      const result = await axios.patch(`${import.meta.env.VITE_API_URL}auth/user/details/address`, {
        addresses: operand
      }, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      const newAddresses:Address[] = result.data.map((address: {
        uuid: string,
        street: string,
        city: string,
        postalCode: string
        state: string,
        country: string
      }) => ({
        ...address,
        id: address.uuid
      }))
      setUserAddresses(newAddresses);
    }
    catch {
      dispatchToast(<Toast>
        <ToastTitle>{t('userSettings.savingFailed')}</ToastTitle>
      </Toast>, {position: 'top-end', intent: 'error'})
    }
  }

  const deleteAddress = async (addressId: number) => {
    const operand = userAddresses.map((address) => ({
      ...address,
      operation: address.id !== addressId ? 'UPDATE' : 'DELETE'
    }));
    try {
      const result = await axios.patch(`${import.meta.env.VITE_API_URL}auth/user/details/address`, {
        addresses: operand
      }, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      })
      const newAddresses:Address[] = result.data.map((address: {
        uuid: string,
        street: string,
        city: string,
        postalCode: string
        state: string,
        country: string
      }) => ({
        ...address,
        id: address.uuid
      }))
      setUserAddresses(newAddresses);

    } catch {
      dispatchToast(<Toast>
        <ToastTitle>{t('userSettings.savingFailed')}</ToastTitle>
      </Toast>, {position: 'top-end', intent: 'error'})
    }
  }

  useEffect(() => {
    const getUserData = async () => {
      try {
        setLoading(true);
        setError(false);
        const result = await axios.get(`${import.meta.env.VITE_API_URL}auth/user/details`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        const newPersonalData:PersonalData = {
          firstName: result.data.firstName ?? '',
          lastName: result.data.lastName ?? '',
          phoneNumber: result.data.phoneNumber ?? '',
          email: result.data.email
        };
        setPersonalData(newPersonalData);
        const newAddresses:Address[] = result.data.addresses.map((address: {
          uuid: string,
          street: string,
          city: string,
          postalCode: string
          state: string,
          country: string
        }) => ({
          ...address,
          id: address.uuid
        }))
        setUserAddresses(newAddresses);
        setLoading(false)
      } catch {
        setLoading(false);
        setError(true);
      }
    }
    getUserData();
  }, [token]);

  const changePersonalDataCallback = async(newPersonalData: PersonalData) => {
    try {
      await axios.patch(`${import.meta.env.VITE_API_URL}auth/user/details/personal-data`, {
        phoneNumber: newPersonalData.phoneNumber,
        firstName: newPersonalData.firstName,
        lastName: newPersonalData.lastName
      }, {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        }
      });
      setPersonalData(newPersonalData);
      dispatchToast(<Toast>
        <ToastTitle>{t('userSettings.savedChanges')}</ToastTitle>
      </Toast>, {position: 'top-end', intent: 'success'})
    } catch {
      dispatchToast(<Toast>
        <ToastTitle>{t('userSettings.savingFailed')}</ToastTitle>
      </Toast>, {position: 'top-end', intent: 'error'})
    }
  }

  return (
    <UserSettingsWrapper>
      <UserSettingsHeader>{t('userSettings.header')}</UserSettingsHeader>
      {
        loading ? (
          <Spinner size='extra-large' label={t('userSettings.loading')} labelPosition='after' />
        ): 
        error ? (
          <Text size={700} align='center'>{t('userSettings.error')}</Text>
        ) :
        (
          <>
            <PersonalData 
              personalData={personalData} 
              changePersonalData={changePersonalDataCallback} 
            />
            <UserAddresses 
              addresses={userAddresses} 
              addNewAddress={addNewAddress}  
              deleteAddress={deleteAddress}
            />
          </>
        )
      }
    </UserSettingsWrapper>
  )
}

export default App;
