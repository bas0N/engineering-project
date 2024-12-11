import { fireEvent, render, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { axe, toHaveNoViolations } from 'jest-axe';
import axios from 'axios';
import {ReviewDisplay} from '../ReviewDisplay'

expect.extend(toHaveNoViolations);
const MOCK_PRODUCT_ID = 'productId';

jest.mock('axios');

const mockedAxios = axios as jest.Mocked<typeof axios>;

describe('Review Display', () => {

    beforeEach(() => {
        jest.clearAllMocks();
    })
    it('Should follow a11y rules', async() => {
        mockedAxios.get.mockResolvedValueOnce({
            data: {
                content: []
            }
        });
        const {container, getByText} = render(<ReviewDisplay
            productId={MOCK_PRODUCT_ID} 
            token={''} 
            reloadTriggerer={false}       
        />);
        expect(await axe(container)).toHaveNoViolations();
        expect(getByText('product.reviews.reviewsTitle'));
    });

    it('Should be displaying Spinner while loading', () => {
        mockedAxios.get.mockResolvedValueOnce({
            data: {
                content: null
            }
        });
        const {getByText} = render(<ReviewDisplay
            productId={MOCK_PRODUCT_ID} 
            token={''} 
            reloadTriggerer={false}       
        />);
        expect(getByText('product.reviews.loadingReviews'));
    });

    it('Should map the reviews properly', async () => {
        const stuff = {data: {
            content: [...new Array(10).fill({
                id: "testId",
                title: "testTitle",
                text: "testText",
                userFirstName: "Lenina",
                userLastName: "Huxley",
                userId: "userId",
                timestamp: "",
                helpfulVote: 14,
                verifiedPurchase: true,
            })].map((elem, ind) => ({
                ...elem,
                title: elem.title+ind,
                id: elem.id+ind
            }))
        }}
        mockedAxios.get.mockResolvedValueOnce(stuff);
        const {findByText, findByTestId} = render(<ReviewDisplay
            productId={MOCK_PRODUCT_ID} 
            token={''} 
            reloadTriggerer={false}       
        />);
        
        expect(await findByText('testTitle0'));
        const paginationSection = await findByTestId('pagination-section');
        expect(paginationSection)
        const rightButton = (paginationSection).children[2] as HTMLButtonElement;
        expect(rightButton).toBeEnabled();
        fireEvent.click(rightButton);
        await waitFor(() => {
            const leftButton = (paginationSection).children[0] as HTMLButtonElement;
            expect(leftButton).toBeEnabled();
            fireEvent.click(leftButton);
            expect(rightButton).toBeEnabled();
        })
    })

    it('Should not shatter into pieces in case of the networ failure', () => {
        const {getByText} = render(<ReviewDisplay
            productId={MOCK_PRODUCT_ID} 
            token={''} 
            reloadTriggerer={false}       
        />);
        expect(getByText('product.reviews.loadingReviews'));
    })
})