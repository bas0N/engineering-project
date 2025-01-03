import { render } from "@testing-library/react";
import { ItemResponse } from "../../Order.types";
import { OrderHistoryTableRow } from "../OrderHistoryTableRow";
import { axe, toHaveNoViolations } from "jest-axe";

expect.extend(toHaveNoViolations);

const MOCK_ITEM:ItemResponse = {
    uuid: "testUUID",
    name: "testName",
    imageUrl: "",
    quantity: 0,
    priceUnit: 0,
    priceSummary: 0
};

describe('Order History Table Row', () => {
    it('Should have no a11y violations', async() => {
        const {container} = render(<OrderHistoryTableRow 
            bgColor='#aaaaaa'
            item={MOCK_ITEM}
        />);
        expect(await axe(container)).toHaveNoViolations();
    });
});