import { axe, toHaveNoViolations } from "jest-axe";
import { render, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import { Categories } from "../Categories";

expect.extend(toHaveNoViolations);

const MOCK_CATEGORIES = ['test', 'test2'];
const MOCK_SET_CATEGORIES = jest.fn();

describe('Categories input', () => {

    beforeEach(() => {
        jest.clearAllMocks();
    });

    it('Should have no a11y violations', async() => {
        const {container} = render(<Categories 
            categories={MOCK_CATEGORIES}
            setCategories={MOCK_SET_CATEGORIES}
        />);

        expect(await axe(container)).toHaveNoViolations();
    });

    it('Should be able to add new category', () => {
        const {getByPlaceholderText} = render(<Categories 
                categories={[]}
                setCategories={MOCK_SET_CATEGORIES}
            />
        );
        const input = getByPlaceholderText('addProduct.categories.newCategoryPlaceholder') as HTMLInputElement;
        expect(input);
        fireEvent.change(input, {target: {value: `testCategory`}});
        fireEvent.keyDown(input, {key: 'Enter', code: 'Enter'});
        expect(MOCK_SET_CATEGORIES).toHaveBeenCalled();
    });

    it('Should be able to delete a category', () => {
        const {getByLabelText} = render(<Categories 
            categories={['test']}
            setCategories={MOCK_SET_CATEGORIES}
        />);
        expect(getByLabelText('remove'));
        fireEvent.click(getByLabelText('remove') as HTMLInputElement);
        expect(MOCK_SET_CATEGORIES).toHaveBeenCalled();
    });

    it('Should not be able to create a category if it already exists', () => {
        const {getByPlaceholderText} = render(<Categories 
                categories={['test']}
                setCategories={MOCK_SET_CATEGORIES}
            />
        );
        const input = getByPlaceholderText('addProduct.categories.newCategoryPlaceholder') as HTMLInputElement;
        expect(input);
        fireEvent.change(input, {target: {value: `test`}});
        fireEvent.keyDown(input, {key: 'Enter', code: 'Enter'});
        expect(MOCK_SET_CATEGORIES).not.toHaveBeenCalled();

    })
})