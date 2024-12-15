import { axe, toHaveNoViolations } from "jest-axe";
import { fireEvent, render } from '@testing-library/react';
import { Address, UserAddresses } from "../UserAddresses";

expect.extend(toHaveNoViolations);

const MOCK_ADDRESSES:Address[] = [{
    street: "testStreet",
    city: "testCity",
    postalCode: "testPostalCode",
    state: "testState",
    country: "testCountry",
    id: 0
}]

const MOCK_ADD_NEW_ADDRESS = jest.fn();
const MOCK_DELETE_ADDRESS = jest.fn();

describe('User addresses', () => {
    beforeEach(() => {
        jest.clearAllMocks();
    })
    it('Should have no a11y violations', async() => {
        const {container, getByText} = render(<UserAddresses 
            addresses={[]} 
            addNewAddress={MOCK_ADD_NEW_ADDRESS} 
            deleteAddress={MOCK_DELETE_ADDRESS} />);
        expect(await axe(container)).toHaveNoViolations();
        expect(getByText('userSettings.noAddressesPresent'));
    });
    it('Should be able to render addresses', () => {
        const {getByText, getByTestId} = render(<UserAddresses 
            addresses={MOCK_ADDRESSES}
            addNewAddress={MOCK_ADD_NEW_ADDRESS}
            deleteAddress={MOCK_DELETE_ADDRESS}
        />);

        expect(getByText('testStreet, testCity, testState'));
        expect(getByText('testPostalCode, testCountry'));

        const deleteButton = getByTestId('deleteButton');
        fireEvent.click(deleteButton as HTMLButtonElement);

        expect(MOCK_DELETE_ADDRESS).toHaveBeenCalled();
    });

    it('Should be able to add new address', () => {
        const {getByText, getByPlaceholderText} = render(<UserAddresses 
            addresses={[]}
            addNewAddress={MOCK_ADD_NEW_ADDRESS}
            deleteAddress={MOCK_DELETE_ADDRESS}
        />);

        const newAddressBtn = getByText('userSettings.addNewAddress');

        expect(newAddressBtn);
        fireEvent.click(newAddressBtn as HTMLButtonElement);

        const addingButton = getByText('userSettings.newAddress.submitAddingNewAddress') as HTMLButtonElement;
        expect(addingButton);

        fireEvent.click(addingButton);
        expect(MOCK_ADD_NEW_ADDRESS).not.toHaveBeenCalled();

        expect(getByPlaceholderText('userSettings.newAddress.streetInput'));
        
        const inputs = ['streetInput', 'stateInput', 'cityInput', 'postalCodeInput', 'countryInput'];

        inputs.forEach((inputName) => {
            const input = getByPlaceholderText(`userSettings.newAddress.${inputName}`) as HTMLInputElement;
            if(inputName === 'postalCodeInput') {
                fireEvent.change(input, {target: {value: '12345'}})
            } else {
                fireEvent.change(input, {target: {value: inputName}})
            }
        });

        fireEvent.click(addingButton);

        expect(MOCK_ADD_NEW_ADDRESS).toHaveBeenCalled();

    });
})