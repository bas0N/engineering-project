import { axe, toHaveNoViolations } from "jest-axe";
import { fireEvent, render } from '@testing-library/react';
import '@testing-library/jest-dom';
import {BasketItems, BasketItemType} from '../BasketItems';
expect.extend(toHaveNoViolations);


const MOCK_BASKET_ITEMS:BasketItemType[] = [{
    uuid: 'testId',
    name: 'testName',
    imageUrl: 'mockImage',
    summaryPrice: 400,
    quantity: 4,
},{
    uuid: 'testId2',
    name: 'testName2',
    imageUrl: 'mockImage2',
    summaryPrice: 300,
    quantity: 4,
},{
    uuid: 'testId3',
    name: 'loremIpsumDolorSitAmetConsectetur',
    imageUrl: 'mockImage2',
    summaryPrice: 300,
    quantity: 4,
}]

const MOCK_DELETE_ITEM_CALLBACK = jest.fn();

describe('Basket items', () => {
    it('Has no a11y violations', async() => {
        const {container} = render(<BasketItems 
            items={MOCK_BASKET_ITEMS} 
            deleteItemCallback={MOCK_DELETE_ITEM_CALLBACK}
        />);
        expect(await axe(container)).toHaveNoViolations();
    });

    it('Should display the \'No items\' banner in case no items provided', () => {
        const {getByText} = render(<BasketItems 
            items={[]}
            deleteItemCallback={MOCK_DELETE_ITEM_CALLBACK}
        />);
        expect(getByText('basket.noItems'));
    })

    it('Should display the adequate number of items', async () => {
        const {queryByText, queryAllByText, queryAllByLabelText} = render(<BasketItems 
            items={MOCK_BASKET_ITEMS} 
            deleteItemCallback={MOCK_DELETE_ITEM_CALLBACK}
        />);
        expect(queryByText('testName')).toBeTruthy();
        expect(queryByText('testName2')).toBeTruthy();
        expect(queryAllByText('loremIpsumDolorSitAmetConsectetur').length).toBeTruthy();
        expect(queryAllByLabelText('basket.deleteButton').length).toEqual(3);

        const clickingElem = queryAllByLabelText('basket.deleteButton')[0];
        await fireEvent.click(clickingElem as HTMLButtonElement);
        expect(MOCK_DELETE_ITEM_CALLBACK).toHaveBeenCalledWith('testId');
    });
});