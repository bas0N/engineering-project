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

const MOCK_CLOSE_LAST_ITEMS = jest.fn();

const MOCK_ITEMS:ItemType[] = [{
    id: 'mockId',
    boughtTogether: null,
    categories: [],
    description: [],
    details: {},
    features: [],
    images: [],
    averageRating: 4.8,
    mainCategory: null,
    parentAsin: 'mockId',
    price: '15.23',
    ratingNumber: 40,
    store: 'alpha',
    title: 'mock item1',
    videos: []
},{
    id: 'mockId2',
    boughtTogether: null,
    categories: [],
    description: [],
    details: {},
    features: [],
    images: [],
    averageRating: null,
    mainCategory: null,
    parentAsin: 'mockId2',
    price: '14.99',
    ratingNumber: null,
    store: 'beta',
    title: 'mock item2',
    videos: []
},];

const TestComponent = () => (
    <MemoryRouter>
        <LastItems 
            items={MOCK_ITEMS} 
            closeLastItems={MOCK_CLOSE_LAST_ITEMS} 
        />
    </MemoryRouter>
);

describe('Search items', () => {

    afterEach(() => {
        jest.clearAllMocks();
    })

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

        fireEvent.click(hoveredName);
        expect(MOCK_CLOSE_LAST_ITEMS).toHaveBeenCalled();
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