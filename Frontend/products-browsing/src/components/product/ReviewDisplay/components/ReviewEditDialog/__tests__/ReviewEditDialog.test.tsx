import { fireEvent, render } from "@testing-library/react"
import { ReviewEditDialog } from "../ReviewEditDialog"
import { axe, toHaveNoViolations } from "jest-axe";

expect.extend(toHaveNoViolations);

const MOCK_SET_EDIT_TITLE = jest.fn();
const MOCK_SET_EDIT_TEXT= jest.fn();
const MOCK_SET_EDIT_RATING = jest.fn();
const MOCK_HANDLE_SAVE_EDIT = jest.fn();
const MOCK_HANDLE_CLOSE_EDIT_DIALOG = jest.fn();
const MOCK_SET_IS_EDIT_DIALOG_OPEN = jest.fn();

describe('Review Edit Dialog', () => {

    beforeEach(() => {
        jest.clearAllMocks();
    })

    it('Should have no a11y violations', async() => {
        const {container, getByText} = render(<ReviewEditDialog 
            isEditDialogOpen={true} 
            editTitle={""} 
            setEditTitle={MOCK_SET_EDIT_TITLE} 
            editText={""} 
            setEditText={MOCK_SET_EDIT_TEXT} 
            editRating={0} 
            setEditRating={MOCK_SET_EDIT_RATING} 
            handleCloseEditDialog={MOCK_HANDLE_CLOSE_EDIT_DIALOG} 
            handleSaveEdit={MOCK_HANDLE_SAVE_EDIT} 
            setIsEditDialogOpen={MOCK_SET_IS_EDIT_DIALOG_OPEN} 
        />);
        expect(await axe(container)).toHaveNoViolations();
        fireEvent.click(getByText('common.cancel') as HTMLButtonElement);
        expect(MOCK_HANDLE_CLOSE_EDIT_DIALOG).toHaveBeenCalled();
        fireEvent.click(getByText('common.save') as HTMLButtonElement);
        expect(MOCK_HANDLE_SAVE_EDIT).toHaveBeenCalled();
    });

    it('Should show no content if the dialog is closed', () => {
        const {queryByText} = render(<ReviewEditDialog 
            isEditDialogOpen={false} 
            editTitle={""} 
            setEditTitle={MOCK_SET_EDIT_TITLE} 
            editText={""} 
            setEditText={MOCK_SET_EDIT_TEXT} 
            editRating={0} 
            setEditRating={MOCK_SET_EDIT_RATING} 
            handleCloseEditDialog={MOCK_HANDLE_CLOSE_EDIT_DIALOG} 
            handleSaveEdit={MOCK_HANDLE_SAVE_EDIT} 
            setIsEditDialogOpen={MOCK_SET_IS_EDIT_DIALOG_OPEN} 
        />);
        expect(queryByText('common.cancel')).toBeFalsy();
    });

    it('Should be able to interact with inputs', () => {
        const {getByPlaceholderText} = render(<ReviewEditDialog 
            isEditDialogOpen={true} 
            editTitle={""} 
            setEditTitle={MOCK_SET_EDIT_TITLE} 
            editText={""} 
            setEditText={MOCK_SET_EDIT_TEXT} 
            editRating={0} 
            setEditRating={MOCK_SET_EDIT_RATING} 
            handleCloseEditDialog={MOCK_HANDLE_CLOSE_EDIT_DIALOG} 
            handleSaveEdit={MOCK_HANDLE_SAVE_EDIT} 
            setIsEditDialogOpen={MOCK_SET_IS_EDIT_DIALOG_OPEN} 
        />);

        const titleInput = getByPlaceholderText('product.reviews.titleInputLabel') as HTMLInputElement;
        const textInput = getByPlaceholderText('product.reviews.textInputLabel') as HTMLInputElement;
        const ratingInput = getByPlaceholderText('product.reviews.ratingLabel') as HTMLInputElement;


        fireEvent.change(titleInput, {target: {value: 'newTitle'}});
        expect(MOCK_SET_EDIT_TITLE).toHaveBeenCalledWith('newTitle');
        fireEvent.change(textInput, {target: {value: 'newText'}});
        expect(MOCK_SET_EDIT_TEXT).toHaveBeenCalledWith('newText');
        fireEvent.change(ratingInput, {target: {value: '4'}});
        expect(MOCK_SET_EDIT_RATING).toHaveBeenCalledWith(4);
    })

    it('Should be able to make use of the actions buttons', () => {
        const {getByText} = render(<ReviewEditDialog 
            isEditDialogOpen={true} 
            editTitle={""} 
            setEditTitle={MOCK_SET_EDIT_TITLE} 
            editText={""} 
            setEditText={MOCK_SET_EDIT_TEXT} 
            editRating={0} 
            setEditRating={MOCK_SET_EDIT_RATING} 
            handleCloseEditDialog={MOCK_HANDLE_CLOSE_EDIT_DIALOG} 
            handleSaveEdit={MOCK_HANDLE_SAVE_EDIT} 
            setIsEditDialogOpen={MOCK_SET_IS_EDIT_DIALOG_OPEN} 
        />);
        fireEvent.click(getByText('common.cancel') as HTMLButtonElement);
        expect(MOCK_HANDLE_CLOSE_EDIT_DIALOG).toHaveBeenCalled();
        fireEvent.click(getByText('common.save') as HTMLButtonElement);
        expect(MOCK_HANDLE_SAVE_EDIT).toHaveBeenCalled();
    });
})