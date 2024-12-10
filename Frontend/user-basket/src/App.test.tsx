import { axe, toHaveNoViolations } from "jest-axe";
import axios from 'axios';
import { render } from '@testing-library/react';
import Basket from './App';

expect.extend(toHaveNoViolations);

jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

jest.mock('./components/BasketSummary/BasketSummary.tsx', () => ({
    BasketSummary: ({orderValue}:{orderValue: number}) => <div>VALUE: {orderValue}</div>,
}));

jest.mock('./components/BasketItems/BasketItems.tsx', () => ({
    BasketItems: () => <div>BasketItems Component</div>,
}));

describe('Basket microfrontend', () => {
    afterEach(() => {
        jest.clearAllMocks();
    });
    it('Should have no a11y violations', async() => {
        const returnData = {data: {basketProducts: [], summaryPrice: 0}};
        mockedAxios.get.mockResolvedValueOnce(returnData);
        const { container } = render(<Basket />);
        expect(await axe(container)).toHaveNoViolations();
    });

    it('Should signalize if the loading failed', async() => {
        mockedAxios.get.mockRejectedValueOnce(new Error('testError'));
        const {findByText} = render(<Basket />);
        expect(findByText('basket.loadingFailed'));
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
        expect(findByText('VALUE: 124'));
        expect(findByText('BasketItems Component'));
    });
});