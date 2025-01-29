import { axe, toHaveNoViolations } from "jest-axe";
import { fireEvent, render } from '@testing-library/react';
import '@testing-library/jest-dom';
import axios from 'axios';
import { ProductsPagerProps } from "../productsPager/ProductsPager";
import { Product, ProductsDisplayProps } from "../productsDisplay/ProductsDisplay";
import ProductsList from "../ProductsList";

expect.extend(toHaveNoViolations);

jest.mock('../productsPager/ProductsPager', () => ({
    ...jest.requireActual('../productsPager/ProductsPager'),
    ProductsPager: (props: ProductsPagerProps) => (<>
        <div>CURRENT PAGE: {props.page}</div>
        <div>CURRENT PRODUCTS NUMBER: {props.currentProductsNumber}</div>
        <button onClick={() => props.changePage(props.page+1)}>
            CHANGE PAGE
        </button>
    </>)
}))

jest.mock('../productsDisplay/ProductsDisplay', () => ({
    ...jest.requireActual('../productsDisplay/ProductsDisplay'),
    ProductsDisplay: (props: ProductsDisplayProps) => (<>
        <div>
            PRODUCTS DISPLAY
        </div>
        <section>
            {props.products.map((product) => (<div>{JSON.stringify(product)}</div>))}
        </section>
        <button onClick={() => props.deleteProduct(props.products[0]?.parentAsin ?? '')}>
            DELETE PRODUCT
        </button>
    </>)
}));

jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

const MOCKED_PRODUCTS:Product[] = [{
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
}]

describe('ProductsList', () => {

    beforeEach(() => {
        jest.clearAllMocks();
    })

    it('Should have no a11y violations', async() => {
        mockedAxios.get.mockResolvedValueOnce({
            data: {
                content: MOCKED_PRODUCTS
            }
        })
        const {container} = render(<ProductsList />);
        expect(await axe(container)).toHaveNoViolations();
    });

    it('Should display the preloader', () => {
        mockedAxios.get.mockResolvedValueOnce({
            data: {
                content: MOCKED_PRODUCTS
            }
        })
        const {getByText} = render(<ProductsList />);
        expect(getByText('productsList.loading'));
    });

    it('Should be able to trigger the deletion of an item', async() => {
        mockedAxios.get.mockResolvedValueOnce({
            data: {
                content: MOCKED_PRODUCTS
            }
        })
        mockedAxios.delete.mockResolvedValueOnce({

        });
        const {findByText} = render(<ProductsList />);
        expect(await findByText(JSON.stringify(MOCKED_PRODUCTS[0])));
        const deleteButton = await findByText('DELETE PRODUCT');
        expect(deleteButton);
        fireEvent.click(deleteButton as HTMLButtonElement);

        expect((await findByText('PRODUCTS DISPLAY') as HTMLElement)?.nextSibling?.childNodes?.length).toEqual(1);
    });

    it('Should be able to change the page', async() => {
        mockedAxios.get.mockResolvedValueOnce({
            data: {
                content: MOCKED_PRODUCTS
            }
        })
        const {findByText} = render(<ProductsList />);
        expect(await findByText(JSON.stringify(MOCKED_PRODUCTS[0])));
        fireEvent.click((await findByText('CHANGE PAGE')) as HTMLButtonElement);
    });

    it('Should be able to handle the network failures of the GET query', async() => {
        mockedAxios.get.mockRejectedValueOnce(new Error('network failure'));
        const {queryByText} = render(<ProductsList />);
        expect(queryByText('PRODUCTS DISPLAY')).toBeFalsy();
    });

    it('Should be able to handle the network failures of the DELETE query', async() => {
        mockedAxios.get.mockResolvedValueOnce({
            data: {
                content: MOCKED_PRODUCTS
            }
        })
        mockedAxios.delete.mockRejectedValueOnce(new Error('network failure'));
        const {findByText} = render(<ProductsList />);
        expect(await findByText(JSON.stringify(MOCKED_PRODUCTS[0])));
        const deleteButton = await findByText('DELETE PRODUCT');
        expect(deleteButton);
        fireEvent.click(deleteButton as HTMLButtonElement);
        expect((await findByText('PRODUCTS DISPLAY') as HTMLElement)?.nextSibling?.childNodes?.length).toEqual(2);
    });
})