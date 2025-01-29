import { axe, toHaveNoViolations } from "jest-axe";
import { render } from '@testing-library/react';
import '@testing-library/jest-dom';
import axios from 'axios';
import { useParams } from 'react-router-dom';
import {ProductImageManagement, ImageType} from "../ProductImageManagement"

expect.extend(toHaveNoViolations);


jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    useParams: jest.fn(),
}));

jest.mock('axios');

const mockedAxios = axios as jest.Mocked<typeof axios>;

const MOCK_IMAGES:ImageType[] = [{
    hiRes: "hiRes",
    large: "large",
    variant: "variant",
    thumb: "thumb"
}];

describe('Product Image Management', () => {

    beforeEach(() => {
        jest.clearAllMocks();
        mockedAxios.get.mockImplementation((url) => {
            const productId = url.split('product/')[1];
            if(productId === 'failure'){
                return Promise.reject(new Error('network failure'));
            }
            return Promise.resolve({
                data: {
                    images: MOCK_IMAGES
                }
            })
        });
        (useParams as jest.Mock).mockReturnValue({ productId: '123' });
    })

    it('Should have no a11y violations', async() => {
        const {container} = render(<ProductImageManagement />);
        expect(await axe(container)).toHaveNoViolations();
    });

    it('Should have handle the missing productId', async() => {
        (useParams as jest.Mock).mockReturnValue({});
        const {findByText} = render(<ProductImageManagement />);
        expect(await findByText('productImageManagement.noProductFound'));
    });
})