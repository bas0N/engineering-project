import { fireEvent, render } from "@testing-library/react"
import { DeliveryMethods } from "../DeliveryMethods"
import { axe, toHaveNoViolations } from "jest-axe";
import { DeliverMethod } from "../../Order/Order.types";

expect.extend(toHaveNoViolations);

const MOCK_ON_CHANGE = jest.fn();
const MOCK_LABEL='label';
const MOCK_DELIVERY_METHODS:DeliverMethod[] = [{
    uuid: "testUUID",
    name: "testName",
    price: 12
}];

describe('Delivery Method', () => {
    it('Should have no a11y violations', async() => {
        const {container} = render(<DeliveryMethods 
            deliverMethods={[]} 
            selectedDeliverId={null} 
            onChange={MOCK_ON_CHANGE} 
            label={MOCK_LABEL} 
        />);
        expect(await axe(container)).toHaveNoViolations();
    });

    it('Should be able to select a delivery method', () => {
        const {getByText} = render(<DeliveryMethods 
            deliverMethods={MOCK_DELIVERY_METHODS} 
            selectedDeliverId={null} 
            onChange={MOCK_ON_CHANGE} 
            label={MOCK_LABEL} 
        />);

        const option = getByText('testName (12 PLN)') as HTMLElement;

        expect(option);

        fireEvent.click(option);

        expect(MOCK_ON_CHANGE).toHaveBeenCalledWith('testUUID');
    })
})