import { axe, toHaveNoViolations } from 'jest-axe';
import { fireEvent, render } from '@testing-library/react';
import { ItemType, LastItems } from '../LastItems';
import { MemoryRouter } from 'react-router';

expect.extend(toHaveNoViolations);

const mockNavigate = jest.fn();
jest.mock('react-router', () => ({
    ...jest.requireActual('react-router'),
    useNavigate: () => mockNavigate,
}));

const MOCK_ITEMS:ItemType[] = [{
    name: 'mock item1',
    image: 'mock',
    no_of_ratings: '40',
    ratings: '4.1',
    id: 'mockId'
},{
    name: 'mock item2',
    image: 'mock',
    no_of_ratings: null,
    ratings: '0',
    id: 'mockId2'
},];

const TestComponent = () => (
    <MemoryRouter>
        <LastItems items={MOCK_ITEMS} />
    </MemoryRouter>
);

describe('Search items', () => {
    it('Should have no a11y violations', async() => {
        const {container} = render(<TestComponent />);
        expect(await axe(container)).toHaveNoViolations();
    });

    it('Should render the list correctly', () => {
        const {queryAllByText} = render(<TestComponent />);

        expect(queryAllByText(/mock item/i)).toHaveLength(2);
    });

    it('navigates to the product page when an item is clicked', () => {
        const {queryByText} = render(
            <TestComponent />
        );

        const item = queryByText('mock item1');
        fireEvent.click(item as HTMLElement);
        expect(mockNavigate).toHaveBeenCalledWith('/products/mockId');
    });

    it('shows hovered item details and handles hover correctly', () => {
        const {getByRole, queryByText, queryAllByText} = render(
            <TestComponent />
        );

        const item = queryByText('mock item2');
        fireEvent.mouseEnter(item as HTMLElement);

        const hoveredName = getByRole('heading', { name: /mock item2/i });
        expect(hoveredName).toBeTruthy();
        expect(queryByText('No ratings yet')).toBeTruthy();

        fireEvent.click(queryAllByText('mock item2')[0] as HTMLElement);
        expect(mockNavigate).toHaveBeenCalledWith('/products/mockId2');
    });

    it('displays rating for items with ratings', () => {
        const {queryByText, queryAllByText} = render(
            <TestComponent />
        );

        fireEvent.mouseEnter(queryByText('mock item1') as HTMLElement);

        expect(queryAllByText('mock item1')).toHaveLength(2);
        expect(queryByText('40')).toBeTruthy();
    });

    it('clears hovered item on mouse leave', () => {
        const {queryByRole, queryByText, queryAllByText} = render(
            <TestComponent />
        );

        fireEvent.mouseEnter(queryByText('mock item1') as HTMLElement);
        const hoveredItem = queryByRole('heading', { name: 'mock item1' });
        expect(hoveredItem).toBeTruthy();

        fireEvent.mouseLeave(queryAllByText('mock item1')[0] as HTMLElement);
        expect(queryByRole('heading', { name: 'mock item1' })).toBeFalsy();
    });
});