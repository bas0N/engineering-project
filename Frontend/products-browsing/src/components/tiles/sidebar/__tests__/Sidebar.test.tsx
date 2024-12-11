import { fireEvent, render } from '@testing-library/react';
import '@testing-library/jest-dom';
import { axe, toHaveNoViolations } from 'jest-axe';
import { Sidebar } from "../Sidebar";

expect.extend(toHaveNoViolations);

const MOCK_CLOSE_SIDEBAR = jest.fn();
const MOCK_ON_CATEGORIES_SELECT = jest.fn();
const MOCK_SELECT_MAX_PRICE = jest.fn();
const MOCK_SELECT_MIN_PRICE = jest.fn();

describe('Sidebar', () => {
    beforeEach(() => {
        jest.clearAllMocks();
    })
    it('Should have no a11y violations', async() => {
        const {container} = render(<Sidebar 
            isOpened={false} 
            closeSidebar={MOCK_CLOSE_SIDEBAR} 
            categories={[]} 
            onCategoriesSelect={MOCK_ON_CATEGORIES_SELECT} 
            minPrice={undefined} 
            maxPrice={undefined} 
            selectMinPrice={MOCK_SELECT_MIN_PRICE} 
            selectMaxPrice={MOCK_SELECT_MAX_PRICE} 
        />);

        expect(await axe(container)).toHaveNoViolations();
    })

    it('Should be able to choose the category of the product', () => {
        const {getByTestId, getByRole} = render(<Sidebar 
            isOpened={false} 
            closeSidebar={MOCK_CLOSE_SIDEBAR} 
            categories={['category1','category2']} 
            onCategoriesSelect={MOCK_ON_CATEGORIES_SELECT} 
            minPrice={undefined} 
            maxPrice={undefined} 
            selectMinPrice={MOCK_SELECT_MIN_PRICE} 
            selectMaxPrice={MOCK_SELECT_MAX_PRICE} 
        />);
        fireEvent.click(getByTestId('categories-dropdown') as HTMLButtonElement)
        fireEvent.click(getByRole('menuitemcheckbox', {name: 'category1'}));
        expect(MOCK_ON_CATEGORIES_SELECT).toHaveBeenCalled();
    })

    it('Should be able to take control of the price', () => {
        const {getByPlaceholderText} = render(<Sidebar 
            isOpened={false} 
            closeSidebar={MOCK_CLOSE_SIDEBAR} 
            categories={['category1','category2']} 
            onCategoriesSelect={MOCK_ON_CATEGORIES_SELECT} 
            minPrice={undefined} 
            maxPrice={undefined} 
            selectMinPrice={MOCK_SELECT_MIN_PRICE} 
            selectMaxPrice={MOCK_SELECT_MAX_PRICE} 
        />);
        const minInput = getByPlaceholderText('tiles.sidebar.minPrice');
        const maxInput = getByPlaceholderText('tiles.sidebar.maxPrice');
        expect(minInput);
        expect(maxInput);

        fireEvent.change(minInput, {target: {value: 100}});
        expect(MOCK_SELECT_MIN_PRICE).toHaveBeenCalled();
        fireEvent.change(maxInput, {target: {value: 200}});
        expect(MOCK_SELECT_MAX_PRICE).toHaveBeenCalled();
    })

    it('Should not be able to change the minPrice if greater than maxPrice', () => {
        const {getByPlaceholderText} = render(<Sidebar 
            isOpened={false} 
            closeSidebar={MOCK_CLOSE_SIDEBAR} 
            categories={['category1','category2']} 
            onCategoriesSelect={MOCK_ON_CATEGORIES_SELECT} 
            minPrice={undefined} 
            maxPrice={400} 
            selectMinPrice={MOCK_SELECT_MIN_PRICE} 
            selectMaxPrice={MOCK_SELECT_MAX_PRICE} 
        />);
        const minInput = getByPlaceholderText('tiles.sidebar.minPrice');
        expect(minInput);
        fireEvent.change(minInput, {target: {value: 500}});
        expect(MOCK_SELECT_MIN_PRICE).not.toHaveBeenCalled();
    })

    it('Should not be able to change the maxPrice if greater than minPrice', () => {
        const {getByPlaceholderText} = render(<Sidebar 
            isOpened={false} 
            closeSidebar={MOCK_CLOSE_SIDEBAR} 
            categories={['category1','category2']} 
            onCategoriesSelect={MOCK_ON_CATEGORIES_SELECT} 
            minPrice={400} 
            maxPrice={undefined} 
            selectMinPrice={MOCK_SELECT_MIN_PRICE} 
            selectMaxPrice={MOCK_SELECT_MAX_PRICE} 
        />);
        const maxInput = getByPlaceholderText('tiles.sidebar.maxPrice');
        expect(maxInput);
        fireEvent.change(maxInput, {target: {value: 100}});
        expect(MOCK_SELECT_MAX_PRICE).not.toHaveBeenCalled();
    })
});