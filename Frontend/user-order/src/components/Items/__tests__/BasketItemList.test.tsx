import { axe, toHaveNoViolations } from "jest-axe";
import { render } from '@testing-library/react';
import { BasketItem } from "../../../Order.types";
import { BasketItemsList } from "../BasketItemList"

expect.extend(toHaveNoViolations);

const MOCK_ITEMS:BasketItem[] = [{
    uuid: "testUUID",
    productId: "testProductId",
    name: "testName",
    quantity: 1,
    imageUrl: "loremIpsum",
    price: 120,
    summaryPrice: 120,
    isActive: false
}];

describe('Basket Item List', () => {
    it('Should have no a11y violations', async() => {
        const {container, getByText} = render(<BasketItemsList items={MOCK_ITEMS} />);
        expect(await axe(container)).toHaveNoViolations();
        expect(getByText('basketItemList.title'));
    });
})