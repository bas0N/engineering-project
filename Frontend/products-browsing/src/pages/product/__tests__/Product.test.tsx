import { fireEvent, render } from '@testing-library/react';
import '@testing-library/jest-dom';
import { axe, toHaveNoViolations } from 'jest-axe';
import axios from 'axios';
import {useParams} from 'react-router-dom';
import Product from '../Product';

expect.extend(toHaveNoViolations);

jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    useParams: jest.fn(),
}));

jest.mock('../../../components/product/ProductPresentation/ProductPresentation', () => ({
    ...jest.requireActual('../../../components/product/ProductPresentation/ProductPresentation'),
    ProductPresentation: ({setIsReviewAdded}:{setIsReviewAdded: () => void}) => (<button onClick={setIsReviewAdded}>
        Open Review
    </button>)
}))

jest.mock('../../../components/product/ReviewForm/ReviewForm', () => ({
    ...jest.requireActual('../../../components/product/ReviewForm/ReviewForm'),
    ReviewForm: ({closeReviewForm}: {closeReviewForm: () => void}) => (<button onClick={closeReviewForm}>
        Review form displayed
    </button>)
}))

jest.mock('../../../components/product/ImagesCarousel/ImagesCarousel', () => ({
    ...jest.requireActual('../../../components/product/ImagesCarousel/ImagesCarousel'),
    ImagesCarousel: () => (<>IMAGES CAROUSEL</>)
}))
jest.mock('../../../components/product/DetailsAndFeatures/DetailsAndFeatures', () => ({
    DetailsAndFeatures: () => (<>DETAILS AND FEATURES</>)
}))

jest.mock('../../../components/product/Recommendations/Recommendations', () => ({
    Recommendations: () => (<>RECOMMENDATIONS</>)
}));

describe('Product', () => {
    beforeEach(() => {
        jest.clearAllMocks();
        (useParams as jest.Mock).mockReturnValue({ productId: '123' });

        Object.defineProperty(window, 'localStorage', {
            value: {
                getItem: jest.fn((key) => mockStorage[key] || null),
                setItem: jest.fn(),
                removeItem: jest.fn(),
                clear: jest.fn(() => {
                mockStorage = {};
                }),
            },
            writable: true,
        });
    
        let mockStorage: Record<string, string> = {
            authToken: 'testtoken'
        };
    })
    it('Should have no a11y violations', ( async() => {
        mockedAxios.get.mockResolvedValue({
            data: {
                id: "testId",
                boughtTogether:  null,
                categories: [],
                description: [],
                details: {},
                features: [],
                images: [],
                averageRating:  null,
                mainCategory:  null,
                parentAsin: 'skzPW',
                price: '124.22',
                ratingNumber: null,
                store: 'testStore',
                title: 'testTitle',
                videos: []
            }
        })
        const {container, getByText, queryByText} = render(<Product />);
        expect(await axe(container)).toHaveNoViolations();
        const reviewButton = getByText('Open Review') as HTMLButtonElement;
        expect(reviewButton)
        fireEvent.click(reviewButton);
        const reviewCloseButton = getByText('Review form displayed') as HTMLButtonElement;
        expect(reviewCloseButton);
        fireEvent.click(reviewCloseButton);

        expect(queryByText('Review form displayed')).toBeFalsy();
    }));

    it('Should not go to display in case of the network failure', () => {
        mockedAxios.get.mockRejectedValueOnce(new Error('error'));
        const {queryByText} = render(<Product />);
        expect(queryByText('Open Review')).toBeFalsy();
    })

});