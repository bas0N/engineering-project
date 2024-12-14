import { axe, toHaveNoViolations } from "jest-axe";
import { fireEvent, render } from '@testing-library/react';
import { PersonalData } from "../PersonalData"

expect.extend(toHaveNoViolations);
const MOCK_PERSONAL_DATA_EMPTY = {
    firstName: "",
    lastName: "",
    phoneNumber: "",
    email: ""
}
const MOCK_PERSONAL_DATA_FILLED = {
    firstName: "Lorem",
    lastName: "ipsum",
    phoneNumber: "+48884104499",
    email: "test@test.pl"
}

const MOCK_CHANGE_PERSONAL_DATA = jest.fn();

describe('Personal Data', () => {

    beforeEach(() => {
        jest.clearAllMocks();
    })

    it('Has no a11y violations', async() => {
        const {container} = render(<PersonalData 
            personalData={MOCK_PERSONAL_DATA_EMPTY} 
            changePersonalData={MOCK_CHANGE_PERSONAL_DATA} 
        />);
        expect(await axe(container)).toHaveNoViolations();
    });

    it('Should display adequate input data', () => {
        const {getByText, getByPlaceholderText} = render(<PersonalData
            personalData={MOCK_PERSONAL_DATA_FILLED}
            changePersonalData={MOCK_CHANGE_PERSONAL_DATA}
        />);

        const firstNameInput = getByPlaceholderText('userSettings.firstName') as HTMLInputElement;
        const lastNameInput = getByPlaceholderText('userSettings.lastName') as HTMLInputElement;
        const phoneNumberInput = getByPlaceholderText('userSettings.phoneNumber') as HTMLInputElement;

        expect(firstNameInput.value).toEqual(MOCK_PERSONAL_DATA_FILLED.firstName);
        expect(lastNameInput.value).toEqual(MOCK_PERSONAL_DATA_FILLED.lastName);
        expect(phoneNumberInput.value).toEqual(MOCK_PERSONAL_DATA_FILLED.phoneNumber);

        fireEvent.change(firstNameInput, {target: {value: 'newName'}});
        fireEvent.change(lastNameInput, {target: {value: 'lastName'}});
        fireEvent.change(phoneNumberInput, {target: {value: '+48123456789'}});
        fireEvent.click(getByText('userSettings.submitChangesToPersonalData') as HTMLButtonElement);
        expect(MOCK_CHANGE_PERSONAL_DATA).toHaveBeenCalledWith({
            firstName: 'newName',
            lastName: 'lastName',
            phoneNumber: "+48123456789",
            email: "test@test.pl"
        })
    }) 
})