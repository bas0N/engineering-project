import { fireEvent, render } from '@testing-library/react';
import '@testing-library/jest-dom';
import { axe, toHaveNoViolations } from 'jest-axe';
import axios from 'axios';
import {ReviewDisplay} from '../ReviewDisplay'
import { ReviewPaginationProps } from '../components/ReviewPagination/ReviewPagination';
import { ParticularReviewDisplayProps, Review } from '../components/ParticularReviewDisplay/ParticularReviewDisplay';
import { ReviewEditDialogProps } from '../components/ReviewEditDialog/ReviewEditDialog';

expect.extend(toHaveNoViolations);
const MOCK_PRODUCT_ID = 'productId';

jest.mock('axios');

const mockedAxios = axios as jest.Mocked<typeof axios>;

jest.mock('../components/ReviewPagination/ReviewPagination', () => ({
    ...jest.requireActual('../components/ReviewPagination/ReviewPagination'),
    ReviewPagination: (props: ReviewPaginationProps) => (<>
        <div>PAGE: {props.pageNumber}</div>
        <button 
            disabled={props.leftPaginationDisabled}
            onClick={() => props.processLeftPage()}>
                LEFT PAGE
        </button>
        <button 
            disabled={props.rightPaginationDisabled}
            onClick={() => props.processRightPage()}>
                RIGHT PAGE
        </button>
    </>)
}));

jest.mock('../components/ParticularReviewDisplay/ParticularReviewDisplay', () => ({
    ...jest.requireActual('../components/ParticularReviewDisplay/ParticularReviewDisplay'),
    ParticularReviewDisplay: (props: ParticularReviewDisplayProps) => (<>
        <div>{JSON.stringify(props.review)}</div>
        <div>STATUS: {props.loggedInUserId === props.review.userId}</div>
        <button onClick={() => props.handleDeleteReview(props.review.id)}>
            DELETE REVIEW {props.review.id}
        </button>
        <button onClick={() => props.handleOpenEditDialog(props.review)}>
            OPEN EDIT DIALOG
        </button>
    </>)
}));

jest.mock('../components/ReviewEditDialog/ReviewEditDialog', () => ({
    ...jest.requireActual('../components/ReviewEditDialog/ReviewEditDialog'),
    ReviewEditDialog: (props: ReviewEditDialogProps) => (<>
        <div>OPENED: {props.isEditDialogOpen === true ? 'true' : 'false'}</div>
        <div>TITLE: {props.editTitle}</div>
        <div>TEXT: {props.editText}</div>
        <div>RATING: {props.editRating}</div>
        <button onClick={() => {
            props.setEditTitle('new');
            props.setEditText('text');
            props.setEditRating(4);
        }}>
            EDIT DIALOG DATA
        </button>
        <button onClick={() => props.setIsEditDialogOpen(!props.isEditDialogOpen)}>
            TRIGGER IS_EDIT_DIALOG_OPEN
        </button>
        <button onClick={() => props.handleCloseEditDialog()}>
            CLOSE EDIT DIALOG
        </button>
        <button onClick={() => props.handleSaveEdit()}>
            SAVE EDIT DIALOG
        </button>
    </>)
}))

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

        const fillment:Review = {
            id: "testId",
            title: "testTitle",
            text: "testText",
            userFirstName: "Lenina",
            userLastName: "Huxley",
            userId: "userId",
            timestamp: "",
            helpfulVote: 14,
            rating: 1,
            verifiedPurchase: true,
        } ;
        const stuff = {data: {
            content: [...new Array(10).fill(fillment)]
            .map((elem, ind) => ({
                ...elem,
                title: elem.title+ind,
                id: elem.id+ind
            }))
        }}
        mockedAxios.get.mockResolvedValue(stuff);
        const {findByText} = render(<ReviewDisplay
            productId={MOCK_PRODUCT_ID} 
            token={''} 
            reloadTriggerer={false}       
        />);
        
        expect(await findByText(JSON.stringify({
            ...fillment,
            title: fillment.title+'0',
            id: fillment.id+'0'
        })));
        const rightButton = await findByText('RIGHT PAGE') as HTMLButtonElement;
        expect(rightButton).toBeEnabled();
        fireEvent.click(rightButton);
        const leftButton =  await findByText('LEFT PAGE') as HTMLButtonElement;
        expect(leftButton).toBeEnabled();
        fireEvent.click(leftButton);
        expect(rightButton).toBeEnabled();
    })

    it('Should not shatter into pieces in case of the network failure', () => {
        const {getByText} = render(<ReviewDisplay
            productId={MOCK_PRODUCT_ID} 
            token={''} 
            reloadTriggerer={false}       
        />);
        expect(getByText('product.reviews.loadingReviews'));
    });

    it('Should be able to delete a review', async() => {
        const fillment:Review = {
            id: "testId",
            title: "testTitle",
            text: "testText",
            userFirstName: "Lenina",
            userLastName: "Huxley",
            userId: "userId",
            timestamp: "",
            helpfulVote: 14,
            rating: 1,
            verifiedPurchase: true,
        };
        mockedAxios.get.mockResolvedValue({
            data:{
                content: [fillment]
            }
        });
        mockedAxios.delete.mockResolvedValueOnce({});
        const {findByText} = render(<ReviewDisplay
            productId={MOCK_PRODUCT_ID} 
            token={''} 
            reloadTriggerer={false}       
        />);
        const deleteButton = await findByText('DELETE REVIEW testId') as HTMLButtonElement;
        fireEvent.click(deleteButton);
        expect(mockedAxios.get).toHaveBeenCalledTimes(2);
        expect(mockedAxios.delete).toHaveBeenCalled();
    });

    it('Should be able to handle the review deletion error', async() => {
        const fillment:Review = {
            id: "testId",
            title: "testTitle",
            text: "testText",
            userFirstName: "Lenina",
            userLastName: "Huxley",
            userId: "userId",
            timestamp: "",
            helpfulVote: 14,
            rating: 1,
            verifiedPurchase: true,
        };
        mockedAxios.get.mockResolvedValue({
            data:{
                content: [fillment]
            }
        });
        mockedAxios.delete.mockRejectedValueOnce(new Error('network failure'));
        const {findByText} = render(<ReviewDisplay
            productId={MOCK_PRODUCT_ID} 
            token={''} 
            reloadTriggerer={false}       
        />);
        const deleteButton = await findByText('DELETE REVIEW testId') as HTMLButtonElement;
        fireEvent.click(deleteButton);
        expect(mockedAxios.delete).toHaveBeenCalled();
    });

    it('Should be able to open the dialog', async() => {
        const fillment:Review = {
            id: "testId",
            title: "testTitle",
            text: "testText",
            userFirstName: "Lenina",
            userLastName: "Huxley",
            userId: "userId",
            timestamp: "",
            helpfulVote: 14,
            rating: 1,
            verifiedPurchase: true,
        };
        mockedAxios.get.mockResolvedValue({
            data:{
                content: [fillment]
            }
        });
        mockedAxios.put.mockResolvedValueOnce({});
        const {findByText} = render(<ReviewDisplay
            productId={MOCK_PRODUCT_ID} 
            token={''} 
            reloadTriggerer={false}       
        />);

        const openDialogButton = await findByText('OPEN EDIT DIALOG') as HTMLButtonElement;
        fireEvent.click(openDialogButton);
        expect(await findByText('OPENED: true'));
        const changeDataButton = await findByText('EDIT DIALOG DATA') as HTMLButtonElement;
        fireEvent.click(changeDataButton);
        const saveButton = await findByText('SAVE EDIT DIALOG') as HTMLButtonElement;
        fireEvent.click(saveButton);
        expect(mockedAxios.put).toHaveBeenCalled();
    });
})