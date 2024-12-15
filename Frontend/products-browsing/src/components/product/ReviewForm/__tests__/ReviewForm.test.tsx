import { fireEvent, render } from '@testing-library/react';
import '@testing-library/jest-dom';
import { axe, toHaveNoViolations } from 'jest-axe';
import axios from 'axios';
import { ReviewForm } from '../ReviewForm';
expect.extend(toHaveNoViolations);

const MOCK_CLOSE_REVIEW_FORM = jest.fn();
const MOCK_PRODUCT_ID = 'productId';

jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

describe('Review Form', () => {

    beforeEach(() => {
        jest.clearAllMocks();
        mockedAxios.post.mockResolvedValueOnce({});
    })

    it('Should have no a11y violations', async () => {
        const {container, getByText} = render(<ReviewForm 
            productId={MOCK_PRODUCT_ID}
            token=""
            closeReviewForm={MOCK_CLOSE_REVIEW_FORM}
        />);
        expect(await axe(container)).toHaveNoViolations();
        expect(getByText('product.addReviewHeader'));
        expect(getByText('product.shareOpinionHeader'));
    });

    it('Should be able to close the form', () => {
        const {queryAllByRole} = render(<ReviewForm 
            productId={MOCK_PRODUCT_ID}
            token=""
            closeReviewForm={MOCK_CLOSE_REVIEW_FORM}
        />);

        const closingButton = queryAllByRole('button')[0] as HTMLButtonElement;

        fireEvent.click(closingButton);
        expect(MOCK_CLOSE_REVIEW_FORM).toHaveBeenCalled();
    });

    it('Should be able to fill out the form', () => {
        const {getByText, getByPlaceholderText, getAllByRole} = render(<ReviewForm 
            productId={MOCK_PRODUCT_ID}
            token=""
            closeReviewForm={MOCK_CLOSE_REVIEW_FORM}
        />);

        const submitButton = getByText('product.addReview') as HTMLButtonElement;
        expect(submitButton).toBeDisabled();

        const titleInput = getByPlaceholderText('product.opinionTitle');
        expect(titleInput);
        expect(submitButton).toBeDisabled();

        fireEvent.change(titleInput, {target: {value: 'test'}});

        const textareaInput = getByPlaceholderText('product.opinionPlaceholder');
        expect(textareaInput);

        fireEvent.change(textareaInput, {target: {value: 'testTextarea 123'}});

        expect(submitButton).toBeDisabled();

        const stars = getAllByRole('radio');
        fireEvent.click(stars[4]);

        expect(submitButton).toBeEnabled();

        fireEvent.click(submitButton);

        expect(mockedAxios.post).toHaveBeenCalled();
    });

    it('Should handle failing the network connection', () => {
        mockedAxios.post.mockRejectedValue({});

        const {getByText, getByPlaceholderText, getAllByRole} = render(<ReviewForm 
            productId={MOCK_PRODUCT_ID}
            token=""
            closeReviewForm={MOCK_CLOSE_REVIEW_FORM}
        />);

        const submitButton = getByText('product.addReview') as HTMLButtonElement;
        expect(submitButton).toBeDisabled();

        const titleInput = getByPlaceholderText('product.opinionTitle');
        expect(titleInput);
        expect(submitButton).toBeDisabled();

        fireEvent.change(titleInput, {target: {value: 'test'}});

        const textareaInput = getByPlaceholderText('product.opinionPlaceholder');
        expect(textareaInput);

        fireEvent.change(textareaInput, {target: {value: 'testTextarea 123'}});

        expect(submitButton).toBeDisabled();

        const stars = getAllByRole('radio');
        fireEvent.click(stars[4]);

        expect(submitButton).toBeEnabled();

        fireEvent.click(submitButton);

        expect(mockedAxios.post).toHaveBeenCalled();
    })
});