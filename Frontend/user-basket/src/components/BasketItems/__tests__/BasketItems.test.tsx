import { axe, toHaveNoViolations } from "jest-axe";
import { render } from '@testing-library/react';
import '@testing-library/jest-dom';
import {BasketItems, BasketItemType} from '../BasketItems';
expect.extend(toHaveNoViolations);


const MOCK_BASKET_ITEMS:BasketItemType[] = [{
    uuid: 'testId',
    name: 'testName',
    image: 'mockImage',
    summaryPrice: 400,
    quantity: 4,
},{
    uuid: 'testId2',
    name: 'testName2',
    image: 'mockImage2',
    summaryPrice: 300,
    quantity: 4,
},{
    uuid: 'testId2',
    name: 'loremIpsumDolorSitAmetConsectetur',
    image: 'mockImage2',
    summaryPrice: 300,
    quantity: 4,
}]

describe('Basket items', () => {
    it('Has no a11y violations', async() => {
        const {container} = render(<BasketItems items={MOCK_BASKET_ITEMS} />);
        expect(await axe(container)).toHaveNoViolations();
    });

    it('Should display the \'No items\' banner in case no items provided', () => {
        const {getByText} = render(<BasketItems items={[]}/>);
        expect(getByText('basket.noItems'));
    })

    it('Should display the adequate number of items', () => {
        const {queryByText, queryAllByText} = render(<BasketItems items={MOCK_BASKET_ITEMS} />);
        expect(queryByText('testName')).toBeTruthy();
        expect(queryByText('testName2')).toBeTruthy();
        expect(queryAllByText('loremIpsumDolorSitAmetConsectetur').length).toBeTruthy();
    });
});