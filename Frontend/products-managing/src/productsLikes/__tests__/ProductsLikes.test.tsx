import { axe, toHaveNoViolations } from "jest-axe";
import { render } from '@testing-library/react';
import '@testing-library/jest-dom';
import axios from 'axios';
import ProductsLikes from "../ProductsLikes";

expect.extend(toHaveNoViolations);

jest.mock('axios');

const mockedAxios = axios as jest.Mocked<typeof axios>;

describe('Products Likes', () => {

    beforeEach(() => {
        jest.clearAllMocks();
    })

    it('Should have no a11y violations', async() => {
        mockedAxios.get.mockResolvedValueOnce({
            data: [{
                title: 'testTitle',
                price: '12.99',
                ratingNumber: 44,
                averageRating: 4.3,
                images: [],
                isActive: true
            },{
                title: 'testTitle',
                price: '12.99',
                ratingNumber: 44,
                averageRating: 4.3,
                images: [],
                isActive: false
            },]
        })
        const {container} = render(<ProductsLikes />);
        expect(await axe(container)).toHaveNoViolations();
    });

    it('Should display the loading spinner', async() => {
        mockedAxios.get.mockResolvedValueOnce({
            data: [{
                title: 'testTitle',
                price: '12.99',
                ratingNumber: 44,
                averageRating: 4.3,
                images: [],
                isActive: true
            }]
        })
        const {getByText} = render(<ProductsLikes />);
        expect(getByText('productsLikes.loading'));
    });

    it('Should handle the data ingestion error', async() => {
        mockedAxios.get.mockRejectedValueOnce(new Error('network failure'));
        const {findByText} = render(<ProductsLikes />);
        expect(await findByText('productsLikes.noProducts'));
    })
})