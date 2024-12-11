import { axe, toHaveNoViolations } from "jest-axe";
import axios from 'axios';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { ProductPresentation } from "../ProductPresentation";

expect.extend(toHaveNoViolations);

const MOCK_SET_IS_REVIEW_ADDED = jest.fn();
const MOCK_PRODUCT_ID = 'productId';
jest.mock('axios');

const handleIsLiked = jest.fn(() => {
    return Promise.resolve({
        data: true
    })
})

const handleNumberOfLikes = jest.fn(() => {
    return Promise.resolve({
        data: 160
    })
});

const handleBasket = jest.fn(() => {
    return Promise.resolve({
        data: {}
    })
});

const handleLiking = jest.fn(() => {
    return Promise.resolve({
        data: {}
    })
});

const handleDeletingLike = jest.fn(() => {
    return Promise.resolve({
        data: {}
    })
})

describe('Product Presentation Component test', () => {

    const mockedGet = axios.get as jest.MockedFunction<typeof axios.get>;
    const mockedPost = axios.post as jest.MockedFunction<typeof axios.post>;
    const mockedDelete = axios.delete as jest.MockedFunction<typeof axios.delete>;

    beforeEach(() => {
        jest.clearAllMocks();
        mockedGet.mockImplementation((url) => {
            const processedUrl = url.split('http://localhost:3001/')[1];
            if(processedUrl === `like/number/${MOCK_PRODUCT_ID}`) return handleNumberOfLikes();
            if(processedUrl === `like/isLiked/${MOCK_PRODUCT_ID}`) return handleIsLiked();
            return Promise.reject(new Error('wrong route'));
        });
        mockedPost.mockImplementation((url) => {
            const processedUrl = url.split('http://localhost:3001/')[1];
            if(processedUrl === 'basket') return handleBasket();
            if(processedUrl === `like/${MOCK_PRODUCT_ID}`) return handleLiking();
            return Promise.reject(new Error('wrong route'));
        });
        mockedDelete.mockImplementation((url) => {
            const processedUrl = url.split('http://localhost:3001/')[1];
            if(processedUrl === `like/remove/${MOCK_PRODUCT_ID}`) return handleDeletingLike();
            return Promise.reject(new Error('wrong route'));
        })
    })

    it('Should have no a11y violations', async () => {
        const {container} = render(<ProductPresentation 
            title='mockTitle'
            categories={['category1', 'category2']}
            ratingNumber={null}
            averageRating={null}
            price='148.2' productId={MOCK_PRODUCT_ID} token={""} 
            setIsReviewAdded={MOCK_SET_IS_REVIEW_ADDED} />
        );
        expect(await axe(container)).toHaveNoViolations();
        expect(screen.getByText('product.noRatings'));
        expect(screen.getByText('category1'));
        expect(screen.getByText('category2'));
    });

    it('Should be able to display the ratings', () => {
        render(<ProductPresentation 
            title='mockTitle' 
            categories={[]} 
            ratingNumber={194} 
            averageRating={4.9} 
            price='148.2' 
            productId={MOCK_PRODUCT_ID} token={""}
            setIsReviewAdded={MOCK_SET_IS_REVIEW_ADDED} />
        );

        expect(screen.getByText('4.9'));
        expect(screen.getByText('194'));
        const reviewBtn = screen.getByText('product.addReview');
        expect(reviewBtn);

        fireEvent.click(reviewBtn);

        expect(MOCK_SET_IS_REVIEW_ADDED).toHaveBeenCalled();
    });

    it('Should be able to like dislike the product and like it back', async () => {
        render(<ProductPresentation 
            title='mockTitle' 
            categories={[]} 
            ratingNumber={194} 
            averageRating={4.9} 
            price='148.2' 
            productId={MOCK_PRODUCT_ID} token={""}
            setIsReviewAdded={MOCK_SET_IS_REVIEW_ADDED} />
        );

        const likingButton = ((screen.getByText('product.addReview') as HTMLElement)?.previousSibling as HTMLElement).children[0];
        
        expect(likingButton);

        fireEvent.click(likingButton as HTMLButtonElement);

        expect(handleLiking).toHaveBeenCalled();

        await waitFor(() => {
            fireEvent.click(likingButton as HTMLButtonElement);
            expect(handleDeletingLike).toHaveBeenCalled();
        });
    });

    it('Should be able to add the product to basket', async () => {
        render(<ProductPresentation 
            title='mockTitle' 
            categories={[]} 
            ratingNumber={194} 
            averageRating={4.9} 
            price='148.2' 
            productId={MOCK_PRODUCT_ID} token={""}
            setIsReviewAdded={MOCK_SET_IS_REVIEW_ADDED} />
        );


        const basketBtn = screen.getByText('product.addToBasket') as HTMLButtonElement;
        expect(basketBtn);
        fireEvent.click(basketBtn);
        expect(handleBasket).not.toHaveBeenCalled();


        const input = screen.getByPlaceholderText('product.selectProductAmount...');
        expect(input);
        fireEvent.change(input, {target: {value: 14}});

        fireEvent.click(basketBtn);

        await waitFor(() => {
            expect(handleBasket).toHaveBeenCalled();
        });
    });

    it('Should be able to handle the connection errors', () => {
        mockedGet.mockImplementation(() => {
            return Promise.reject(new Error('wrong route'));
        });
        mockedPost.mockImplementation(() => {
            return Promise.reject(new Error('wrong route'));
        });
        mockedDelete.mockImplementation(() => {
            return Promise.reject(new Error('wrong route'));
        });

        render(<ProductPresentation 
            title='mockTitle' 
            categories={[]} 
            ratingNumber={194} 
            averageRating={4.9} 
            price='148.2' 
            productId={MOCK_PRODUCT_ID} token={""}
            setIsReviewAdded={MOCK_SET_IS_REVIEW_ADDED} />
        );

        const basketBtn = screen.getByText('product.addToBasket') as HTMLButtonElement;
        expect(basketBtn);

        const input = screen.getByPlaceholderText('product.selectProductAmount...');
        expect(input);
        fireEvent.change(input, {target: {value: 14}});
        fireEvent.click(basketBtn);

        const likingButton = ((screen.getByText('product.addReview') as HTMLElement)?.previousSibling as HTMLElement).children[0];
        
        expect(likingButton);

        fireEvent.click(likingButton as HTMLButtonElement);
    });
})