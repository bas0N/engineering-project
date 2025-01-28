import { axe, toHaveNoViolations } from "jest-axe";
import axios from 'axios';
import { render, fireEvent, waitFor } from '@testing-library/react';
import Basket from './App';
import { BasketItemsProps } from "./components/BasketItems/BasketItems";

expect.extend(toHaveNoViolations);

jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

jest.mock('./components/BasketSummary/BasketSummary.tsx', () => ({
    BasketSummary: ({orderValue}:{orderValue: number}) => <div>VALUE: {orderValue}</div>,
}));

jest.mock('./components/BasketItems/BasketItems.tsx', () => ({
    ...jest.requireActual('./components/BasketItems/BasketItems.tsx'),
    BasketItems: (props: BasketItemsProps) => <>
        <div>BasketItems Component</div>
        <section>
            {props.items.map((elem) => <div onClick={() => props.deleteItemCallback(elem.uuid)}>
                {JSON.stringify(elem)}
            </div>)}
        </section>
    </>,
}));

describe('Basket microfrontend', () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });
    it('Should have no a11y violations', async() => {
        const returnData = {data: {basketProducts: [], summaryPrice: 0}};
        mockedAxios.get.mockResolvedValueOnce(returnData);
        const { container } = render(<Basket />);
        expect(await axe(container)).toHaveNoViolations();
    });

    it('Should not render anything in case no items in the basket', async() => {
        const returnData = {data: {basketProducts: null, summaryPrice: 0}};
        mockedAxios.get.mockResolvedValue(returnData);
        const {findByText} = render(<Basket />);
        waitFor(async() => {
            expect(await findByText('basket.noItems'));
        })
    });

    it('Should signalize if the loading failed', async() => {
        mockedAxios.get.mockRejectedValueOnce(new Error('testError'));
        const {getByText} = render(<Basket />);
        waitFor(() => {
            expect(getByText('basket.loadingFailed'));
        });
    });

    it('Renders everything accordingly', async() => {
        const returnData = {data: {basketProducts: [{
            uuid: 'testId2',
            name: 'loremIpsumDolorSitAmetConsectetur',
            image: 'mockImage2',
            summaryPrice: 300,
            quantity: 4,
        }], summaryPrice: 124}};
        mockedAxios.get.mockResolvedValueOnce(returnData);
        const {findByText, getByText} = render(<Basket />);
        expect(getByText('basket.basketHeader'));
        expect(await findByText('VALUE: 124'));
        expect(await findByText('BasketItems Component'));
    });

    it('Should be able to delete an item', async() => {
        mockedAxios.delete.mockResolvedValueOnce({});
        const returnData = {data: {basketProducts: [{
            uuid: 'testId2',
            name: 'loremIpsumDolorSitAmetConsectetur',
            image: 'mockImage2',
            summaryPrice: 300,
            quantity: 4,
        }], summaryPrice: 124}};
        mockedAxios.get.mockResolvedValue(returnData);
        const {findByText} = render(<Basket />);

        const deletionButton = await findByText(JSON.stringify({
            uuid: 'testId2',
            name: 'loremIpsumDolorSitAmetConsectetur',
            image: 'mockImage2',
            summaryPrice: 300,
            quantity: 4,
        })) as HTMLButtonElement;
        expect(deletionButton);

        fireEvent.click(deletionButton);
        expect(mockedAxios.delete).toHaveBeenCalled();
        expect(mockedAxios.get).toHaveBeenCalledTimes(2);
    });

    it('Should be able to handle error deletion', async() => {
        mockedAxios.delete.mockRejectedValueOnce(new Error('network failure'));
        const returnData = {data: {basketProducts: [{
            uuid: 'testId2',
            name: 'loremIpsumDolorSitAmetConsectetur',
            image: 'mockImage2',
            summaryPrice: 300,
            quantity: 4,
        }], summaryPrice: 124}};
        mockedAxios.get.mockResolvedValue(returnData);
        const {findByText} = render(<Basket />);

        const deletionButton = await findByText(JSON.stringify({
            uuid: 'testId2',
            name: 'loremIpsumDolorSitAmetConsectetur',
            image: 'mockImage2',
            summaryPrice: 300,
            quantity: 4,
        })) as HTMLButtonElement;
        expect(deletionButton);

        fireEvent.click(deletionButton);
    });
});