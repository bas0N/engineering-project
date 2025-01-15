import { axe, toHaveNoViolations } from "jest-axe";
import { render, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import { useNavigate } from 'react-router-dom';
import { Product, ProductsDisplay } from '../ProductsDisplay';

expect.extend(toHaveNoViolations);

jest.mock('react-router-dom', () => ({
    useNavigate: jest.fn(),
}));

const MOCK_PRODUCTS:Product[] = [{
    parentAsin: "test123",
    categories: ['testCategory'],
    mainCategory: "testCategory",
    details: {
        'DETAIL': 'detail'
    },
    features: ['feature'],
    description: ['lorem ipsum', 'dolor sit amet'],
    price: "12.99",
    store: "store",
    title: "productTitle",
    ratingNumber: 2,
    averageRating: 1
},{
    parentAsin: "test1234",
    categories: ['testCategorya'],
    mainCategory: "testCategorya",
    details: {
        'DETAIL': 'detail'
    },
    features: ['feature'],
    description: ['lorem ipsum', 'dolor sit amet'],
    price: "10.23",
    store: "abcd",
    title: "productTitle2",
    ratingNumber: 1,
    averageRating: 0
},{
    parentAsin: "test12345",
    categories: ['testCategorya'],
    mainCategory: "testCategorya",
    details: {
        'DETAIL': 'detail'
    },
    features: ['feature'],
    description: ['lorem ipsum', 'dolor sit amet'],
    price: "14.23",
    store: "store",
    title: "newProduct",
    ratingNumber: 4,
    averageRating: 9
},];

const MOCK_DELETE_PRODUCT = jest.fn();

describe('Products Display', () => {

    beforeEach(() => {
        jest.clearAllMocks();
    })

    it('Should have no a11y violations', async() => {
        const {container} = render(<ProductsDisplay 
            products={MOCK_PRODUCTS} 
            deleteProduct={MOCK_DELETE_PRODUCT}
        />);
        expect(await axe(container)).toHaveNoViolations();
    });

    it('Should be able to use the sorting functions', () => {
        const {getByText} = render(<ProductsDisplay
            products={MOCK_PRODUCTS}
            deleteProduct={MOCK_DELETE_PRODUCT}
            />
        );

        fireEvent.click(getByText('productsList.title') as HTMLElement);
        fireEvent.click(getByText('productsList.numberOfRatings') as HTMLElement);
        fireEvent.click(getByText('productsList.averageRating') as HTMLElement);
        fireEvent.click(getByText('productsList.store') as HTMLElement);
        fireEvent.click(getByText('productsList.price') as HTMLElement);
    })

    it('Should be able to select a product and manipulate it', () => {

        const mockedNavigate = jest.fn();
        (useNavigate as jest.Mock).mockReturnValue(mockedNavigate);

        const {getByText} = render(<ProductsDisplay 
            products={MOCK_PRODUCTS} 
            deleteProduct={MOCK_DELETE_PRODUCT}
        />);

        const buttonCheckout = getByText('productsList.actions.checkout') as HTMLButtonElement;
        const buttonDelete = getByText('productsList.actions.delete') as HTMLButtonElement;
        expect(buttonCheckout).toBeDisabled();
        expect(buttonDelete).toBeDisabled();

        fireEvent.click(getByText('productTitle') as HTMLElement);

        expect(buttonCheckout).toBeEnabled();
        expect(buttonDelete).toBeEnabled();

        fireEvent.click(buttonCheckout);
        expect(mockedNavigate).toHaveBeenCalled();
        
        fireEvent.click(buttonDelete);
        expect(MOCK_DELETE_PRODUCT).toHaveBeenCalled();
    });
})