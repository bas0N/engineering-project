import { fireEvent, render } from "@testing-library/react"
import { axe, toHaveNoViolations } from "jest-axe";
import { AddressForm } from "../AddressForm";
import { AddressRequest } from "../../Order/Order.types";

expect.extend(toHaveNoViolations);

const MOCK_ADDRESS:AddressRequest = {
    street: "testStreet",
    city: "testCity",
    state: "testState",
    postalCode: "testPostalCode",
    country: "testCountry"
};

const MOCK_SET_ADDRESS = jest.fn();

describe('Address Form', () => {
    it('Should have no a11y violations', async() => {
        const {container, getByText} = render(<AddressForm 
            address={MOCK_ADDRESS} 
            setAddress={MOCK_SET_ADDRESS} 
            labelStreet={"Street"} 
            labelCity={"City"} 
            labelState={"State"} 
            labelPostalCode={"Postal Code"} 
            labelCountry={"Country"} 
        />);
        expect(await axe(container)).toHaveNoViolations();
        expect(getByText('order.address.everythingRequired'));
    });

    it('Should be able to change the value of the address', () => {
        const {getByPlaceholderText} = render(<AddressForm 
            address={MOCK_ADDRESS} 
            setAddress={MOCK_SET_ADDRESS} 
            labelStreet={"Street"} 
            labelCity={"City"} 
            labelState={"State"} 
            labelPostalCode={"Postal Code"} 
            labelCountry={"Country"} 
        />);

        const inputPlaceholders = ['street', 'state', 'city', 'xx-xxx', 'country'];
        inputPlaceholders.forEach((inputName) => {
            const input = getByPlaceholderText(inputName === 'xx-xxx' ? inputName : `order.address.${inputName}Placeholder`) as HTMLInputElement;
            expect(input);
            fireEvent.change(input, {target: {value: inputName === 'xx-xxx' ? '12-345' : 'newValue'}});
            expect(MOCK_SET_ADDRESS).toHaveBeenCalledWith({...MOCK_ADDRESS, [ inputName === 'xx-xxx' ? 'postalCode' : inputName]: inputName === 'xx-xxx' ? '12-345' : 'newValue'});
            jest.clearAllMocks();
        })
    });
})