import { axe, toHaveNoViolations } from "jest-axe";
import { fireEvent, render, waitFor } from '@testing-library/react';
import axios from "axios";
import { PersonalData } from './components/PersonalData/PersonalData';
import { NewAddress } from './components/UserAddresses/UserAddresses';
import { App } from './App'; 

jest.mock('axios');

expect.extend(toHaveNoViolations);

const mockedAxios = axios as jest.Mocked<typeof axios>;

jest.mock('./components/PersonalData/PersonalData', () => ({
    ...jest.requireActual('./components/PersonalData/PersonalData'),
    PersonalData: ({changePersonalData}:
        {changePersonalData: (newPersonalData: PersonalData) => void}
    ) => (<button onClick={() => changePersonalData({
        firstName: 'alfa',
        lastName: 'beta',
        phoneNumber:'+4812312313',
        email: 'test@test.pl'
    })}>PERSONAL DATA</button>)
}));

jest.mock('./components/UserAddresses/UserAddresses', () => ({
    ...jest.requireActual('./components/UserAddresses/UserAddresses'),
    UserAddresses: ({addNewAddress, deleteAddress}: {
        addNewAddress: (newAddress: NewAddress) => void;
        deleteAddress: (addressId: number) => void;
    }) => (<>
        <button onClick={() => addNewAddress({
            street: "testStreet",
            city: "testCity",
            postalCode: "12345",
            state: "testState",
            country: "testCountry"
        })}>
            ADD NEW ADDRESS
        </button>
        <button onClick={() => deleteAddress(0)}>DELETE ADDRESS</button>
    </>)
}));

const mockHandlePersonalDataChange = jest.fn(() => {
    return Promise.resolve({
        data: {
            firstName: '',
            lastName: '',
            phoneNumber: '',
            email: 'test@test.pl',
            addresses: []
        }
    });
});

const mockHandleAddingNewAddress = jest.fn(() => {
    return Promise.resolve({
        data: [{
            street: "testStreet",
            city: "testCity",
            postalCode: "12345",
            state: "testState",
            country: "testCountry",
            uuid: '1'
        }]
    })
});

const mockHandleDeletingAddress = jest.fn(() => {
    return Promise.resolve({
        data: []
    })
})

const MOCK_AXIOS_GET_DATA = {
    data: {
        firstName: "string",
        lastName: "string",
        email: "string",
        phoneNumber: "string",
        addresses: [
            {
                uuid: "string",
                street: "string",
                city: "string",
                state: "string",
                postalCode: "string",
                country: "string"
            }
        ],
    }
}

describe('User settings app', () => {
    beforeEach(() => {
        jest.clearAllMocks();
    })
    it('Should have no a11y violations', async() => {
        mockedAxios.get.mockResolvedValueOnce(MOCK_AXIOS_GET_DATA)
        const {container, getByText} = render(<App />);
        expect(await axe(container)).toHaveNoViolations();
        expect(getByText('userSettings.header'))
    });

    it('Should handle the loading failure accordingly', async () => {
        mockedAxios.get.mockRejectedValueOnce(new Error('no connection'));
        const {getByText} = render(<App />);
        await waitFor(() => {
            expect(getByText('userSettings.error'));
        })
    })

    it('Should handle the change of personal data accordingly', async() => {
        mockedAxios.get.mockResolvedValueOnce(MOCK_AXIOS_GET_DATA)
        mockedAxios.patch.mockImplementation((url) => {
            const processedUrl = url.split('http://localhost:3001/')[1];
            if(processedUrl === 'auth/user/details/personal-data') {
                return mockHandlePersonalDataChange();
            } 
            return Promise.reject(new Error('wrong route'))
        })
        const {getByText} = render(<App />);
        await waitFor(() => {
            expect(getByText('PERSONAL DATA'));
            const personalDataButton = getByText('PERSONAL DATA') as HTMLButtonElement;
            fireEvent.click(personalDataButton);
            expect(mockHandlePersonalDataChange).toHaveBeenCalled();
        });
    });


    it('Should handle the network failure in case of personal data change', async() => {
        mockedAxios.get.mockResolvedValueOnce(MOCK_AXIOS_GET_DATA)
        mockedAxios.patch.mockImplementation((url) => {
            const processedUrl = url.split('http://localhost:3001/')[1];
            if(processedUrl === 'auth/user/details/personal-data2') {
                return mockHandlePersonalDataChange();
            } 
            return Promise.reject(new Error('wrong route'))
        })
        const {getByText} = render(<App />);
        await waitFor(() => {
            expect(getByText('PERSONAL DATA'));
            const personalDataButton = getByText('PERSONAL DATA') as HTMLButtonElement;
            fireEvent.click(personalDataButton);
            expect(mockHandlePersonalDataChange).not.toHaveBeenCalled();
        });
    });

    it('Should be able to add new address', async() => {
        mockedAxios.get.mockResolvedValueOnce(MOCK_AXIOS_GET_DATA);
        mockedAxios.patch.mockImplementation((url) => {
            const processedUrl = url.split('http://localhost:3001/')[1];
            if(processedUrl === 'auth/user/details/address') {
                return mockHandleAddingNewAddress();
            } 
            return Promise.reject(new Error('wrong route'))
        });
        const {getByText} = render(<App />);
        await waitFor(() => {
            expect(getByText('ADD NEW ADDRESS'));
            const addAddressButton = getByText('ADD NEW ADDRESS') as HTMLButtonElement;
            fireEvent.click(addAddressButton);
            expect(mockHandleAddingNewAddress).toHaveBeenCalled();
        });
    });

    it('Should be able to withstand the address adding failure', async () => {
        mockedAxios.get.mockResolvedValueOnce(MOCK_AXIOS_GET_DATA);
        mockedAxios.patch.mockRejectedValueOnce(new Error('adding error'));
        const {getByText} = render(<App />);
        await waitFor(() => {
            expect(getByText('ADD NEW ADDRESS'));
            const addAddressButton = getByText('ADD NEW ADDRESS') as HTMLButtonElement;
            fireEvent.click(addAddressButton);
            expect(mockHandleAddingNewAddress).not.toHaveBeenCalled();
        });

    });

    it('Should be able to delete the present address', async() => {
        mockedAxios.get.mockResolvedValueOnce({
            data: {
                ...MOCK_AXIOS_GET_DATA['data'],
                addresses: [{
                    uuid: 0,
                    street: 'testStreet',
                    city: 'testCity',
                    postalCode: '12345',
                    state: 'testState',
                    country: 'testCountry'
                },{
                    uuid: 1,
                    street: 'testStreet',
                    city: 'testCity',
                    postalCode: '12345',
                    state: 'testState',
                    country: 'testCountry'
                },]
            }
        });
        mockedAxios.patch.mockImplementation((url) => {
            const processedUrl = url.split('http://localhost:3001/')[1];
            if(processedUrl === 'auth/user/details/address') {
                return mockHandleDeletingAddress();
            } 
            return Promise.reject(new Error('wrong route'))
        });
        const {getByText} = render(<App />);
        await waitFor(() => {
            expect(getByText('DELETE ADDRESS'));
            const addAddressButton = getByText('DELETE ADDRESS') as HTMLButtonElement;
            fireEvent.click(addAddressButton);
            expect(mockHandleDeletingAddress).toHaveBeenCalled();
        });
    });

    it('Should be able to handle the present address deletion error', async() => {
        mockedAxios.get.mockResolvedValueOnce({
            data: {
                ...MOCK_AXIOS_GET_DATA['data'],
                addresses: [{
                    uuid: 0,
                    street: 'testStreet',
                    city: 'testCity',
                    postalCode: '12345',
                    state: 'testState',
                    country: 'testCountry'
                }]
            }
        });
        mockedAxios.patch.mockRejectedValueOnce(new Error('deleting error'))
        const {getByText} = render(<App />);
        await waitFor(() => {
            expect(getByText('DELETE ADDRESS'));
            const addAddressButton = getByText('DELETE ADDRESS') as HTMLButtonElement;
            fireEvent.click(addAddressButton);
            expect(mockHandleDeletingAddress).not.toHaveBeenCalled();
        });
    });
})