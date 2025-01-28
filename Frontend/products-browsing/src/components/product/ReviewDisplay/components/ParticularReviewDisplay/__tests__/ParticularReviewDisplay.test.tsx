import { render, fireEvent } from "@testing-library/react"
import { Review, ParticularReviewDisplay } from "../ParticularReviewDisplay"
import { axe, toHaveNoViolations } from "jest-axe";

expect.extend(toHaveNoViolations);

const MOCK_HANDLE_OPEN_EDIT_DIALOG = jest.fn();
const MOCK_HANDLE_DELETE_REVIEW = jest.fn();

const MOCK_REVIEW:Review = {
    id: "testId",
    title: "testTitle",
    text: "lorem Ipsum",
    rating: 0,
    userFirstName: null,
    userLastName: null,
    userId: "abc",
    timestamp: "",
    helpfulVote: 0,
    verifiedPurchase: false
};

describe('ParticularReviewDisplay', () => {

    beforeEach(() => {
        jest.clearAllMocks();
    })

    it('Should have no a11y violations', async() => {
        const {container, getByLabelText} = render(<ParticularReviewDisplay
            loggedInUserId={'abc'}
            review={MOCK_REVIEW}
            handleOpenEditDialog={MOCK_HANDLE_OPEN_EDIT_DIALOG}
            handleDeleteReview={MOCK_HANDLE_DELETE_REVIEW}
        />);
        expect(await axe(container)).toHaveNoViolations();
        fireEvent.click(getByLabelText('product.reviews.editReviewTooltip') as HTMLButtonElement);
        expect(MOCK_HANDLE_OPEN_EDIT_DIALOG).toHaveBeenCalled();
        fireEvent.click(getByLabelText('product.reviews.deleteReviewTooltip') as HTMLButtonElement);
        expect(MOCK_HANDLE_DELETE_REVIEW).toHaveBeenCalled();
    });

    it('Should not display the management buttons if the user does not own the review', () => {
        const {queryAllByLabelText} = render(<ParticularReviewDisplay
            loggedInUserId={'ab'}
            review={MOCK_REVIEW}
            handleOpenEditDialog={MOCK_HANDLE_OPEN_EDIT_DIALOG}
            handleDeleteReview={MOCK_HANDLE_DELETE_REVIEW}
        />);
        expect(queryAllByLabelText('product.reviews.editReviewTooltip').length).toBe(0);
    });

    it('Should display the information about the verified purchase', () => {
        const {getByLabelText} = render(<ParticularReviewDisplay
            loggedInUserId={'ab'}
            review={{
                ...MOCK_REVIEW, 
                verifiedPurchase: true
            }}
            handleOpenEditDialog={MOCK_HANDLE_OPEN_EDIT_DIALOG}
            handleDeleteReview={MOCK_HANDLE_DELETE_REVIEW}
        />);
        expect(getByLabelText('product.reviews.verifiedPurchase'));
    });

    it('Should display the name and surname of the user if provided', () => {
        const {queryByText} = render(<ParticularReviewDisplay
            loggedInUserId={'ab'}
            review={{
                ...MOCK_REVIEW,
                userFirstName: 'lorem', 
                userLastName: 'ipsum'
            }}
            handleOpenEditDialog={MOCK_HANDLE_OPEN_EDIT_DIALOG}
            handleDeleteReview={MOCK_HANDLE_DELETE_REVIEW}
        />);

        expect(queryByText(/lorem-ipsum/i)).toBeTruthy();
    });
})