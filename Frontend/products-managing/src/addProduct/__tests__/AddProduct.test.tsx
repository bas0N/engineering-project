import { axe, toHaveNoViolations } from "jest-axe";
import { render, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import AddProduct from '../AddProduct';
import { CategoriesProps } from '../categories/Categories';
import { DetailsProps } from '../details/Details';
import { FeaturesProps } from '../features/Features';
import { FinalizationProps } from '../finalization/Finalization';

expect.extend(toHaveNoViolations);

jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

jest.mock('react-router-dom', () => ({
    useNavigate: jest.fn(),
}));

jest.mock('../categories/Categories', () => ({
    ...jest.requireActual('../categories/Categories'),
    Categories: (props: CategoriesProps) => (
        <>
            <div>{props.categories.map((category) => <div>{category}</div>)}</div>
            <button onClick={() => props.setCategories(['lorem','ipsum'])}>SET CATEGORIES</button>
        </>
    )
}));

jest.mock('../details/Details', () => ({
    ...jest.requireActual('../details/Details'),
    Details: (props: DetailsProps) => (
        <>
            <div>
                {JSON.stringify(props.details)}
            </div>
            <button onClick={() => props.setDetails({'DEPARTMENT': 'test'})}>SET DETAILS</button>
        </>
    )
}));

jest.mock('../features/Features', () => ({
    ...jest.requireActual('../features/Features'), 
    Features: (props: FeaturesProps) => (
        <>
            <div>
                {props.features.map((elem) => <span>{elem}</span>)}
            </div>
            <button onClick={() => props.setFeatures(['cool feature'])}>
                SET FEATURES
            </button>
        </>
    )
}));

jest.mock('../finalization/Finalization', () => ({
    ...jest.requireActual('../finalization/Finalization'),
    Finalization: (props: FinalizationProps) => (<>
        <div>TITLE: {props.title}</div>
        <div>
            DETAILS: {Object.keys(props.details).map((detailName) => <span>{detailName}:{props.details[detailName]}</span>)}
        </div>
        <div>
            DESCRIPTION: {props.description}
        </div>
        <div>
            FEATURES: {props.features.map((feature) => <span>{feature}</span>)}
        </div>
        <div>
            CATEGORIES: {props.categories.map((category) => <span>{category}</span>)}
        </div>
    </>)
}))
    

describe('Add product', () => {

    beforeEach(() => {
        jest.clearAllMocks();
    })

    it('Should have no a11y violations', async() => {
        const {container} = render(<AddProduct />);
        expect(await axe(container)).toHaveNoViolations();
    });

    it('Should be able to process the creation of new product', async () => {
        mockedAxios.post.mockResolvedValueOnce({
            data: {
                parentAsin: 'test123'
            }
        })

        const mockedNavigate = jest.fn();
        (useNavigate as jest.Mock).mockReturnValue(mockedNavigate);
        const {getByText, getByPlaceholderText, getByRole, findByText} = render(<AddProduct />);
        expect(getByText('addProduct.title'));
        
        const titleInput = getByPlaceholderText('addProduct.titlePlaceholder') as HTMLInputElement;
        const descInput = getByPlaceholderText('addProduct.descriptionPlaceholder') as HTMLTextAreaElement;
        expect(titleInput);
        expect(descInput);

        fireEvent.change(titleInput, {target: {value: 'testTitle'}});
        fireEvent.change(descInput, {target: {value: 'testDescription'}});
        fireEvent.click(getByText('SET CATEGORIES') as HTMLButtonElement);
        fireEvent.click(getByText('SET DETAILS') as HTMLButtonElement);
        fireEvent.click(getByText('SET FEATURES') as HTMLButtonElement);
        fireEvent.click(getByText('addProduct.finalizeCreation') as HTMLButtonElement);
        
        expect(getByText('addProduct.finalizationHeader'));
        expect(getByText('lorem'));
        expect(getByText('ipsum'));
        expect(getByText('cool feature'));
        
        const mainCategoryDropdown = getByRole('combobox');
        expect(mainCategoryDropdown);
        fireEvent.click(mainCategoryDropdown as HTMLButtonElement);
        const mainCategoryOption = getByRole('option', {name: 'lorem'});
        expect(mainCategoryOption);
        fireEvent.click(mainCategoryOption);
        
        const priceInput = getByPlaceholderText('addProduct.pricePlaceholder') as HTMLInputElement;
        fireEvent.change(priceInput, {target: {value: '12,99'}});

        const storeInput = getByPlaceholderText('addProduct.storePlaceholder') as HTMLInputElement;
        fireEvent.change(storeInput, {target: {value: 'testStore'}});

        fireEvent.click(getByText('addProduct.goBack') as HTMLButtonElement);
        expect(getByText('addProduct.title'));

        fireEvent.click(getByText('addProduct.finalizeCreation') as HTMLButtonElement);
        fireEvent.click(getByText('addProduct.finalizeProductCreation') as HTMLButtonElement);

        expect(await findByText('addProduct.productAdded'));

        fireEvent.click(getByText('addProduct.userProductsNavigation') as HTMLButtonElement);
        expect(mockedNavigate).toHaveBeenCalledWith('/products/mine');

        fireEvent.click(getByText('addProduct.productInspection') as HTMLButtonElement);
        expect(mockedNavigate).toHaveBeenCalledWith('/products/mine/test123');
    });

    it('Should be able to handle the network failure', () => {
        mockedAxios.post.mockRejectedValueOnce(new Error('network failure'));
        const {getByText, getByPlaceholderText, getByRole, queryByText} = render(<AddProduct />);
        expect(getByText('addProduct.title'));
        
        const titleInput = getByPlaceholderText('addProduct.titlePlaceholder') as HTMLInputElement;
        const descInput = getByPlaceholderText('addProduct.descriptionPlaceholder') as HTMLTextAreaElement;
        expect(titleInput);
        expect(descInput);

        fireEvent.change(titleInput, {target: {value: 'testTitle'}});
        fireEvent.change(descInput, {target: {value: 'testDescription'}});
        fireEvent.click(getByText('SET CATEGORIES') as HTMLButtonElement);
        fireEvent.click(getByText('SET DETAILS') as HTMLButtonElement);
        fireEvent.click(getByText('SET FEATURES') as HTMLButtonElement);
        fireEvent.click(getByText('addProduct.finalizeCreation') as HTMLButtonElement);
        
        expect(queryByText(/DESCRIPTION:/i));
        expect(queryByText(/DETAILS/i));
        expect(queryByText(/FEATURES/i));
        expect(queryByText(/TITLE/i));
        expect(queryByText(/CATEGORIES/i));
        
        const mainCategoryDropdown = getByRole('combobox');
        expect(mainCategoryDropdown);
        fireEvent.click(mainCategoryDropdown as HTMLButtonElement);
        const mainCategoryOption = getByRole('option', {name: 'lorem'});
        expect(mainCategoryOption);
        fireEvent.click(mainCategoryOption);
        
        const priceInput = getByPlaceholderText('addProduct.pricePlaceholder') as HTMLInputElement;
        fireEvent.change(priceInput, {target: {value: '12,99'}});

        const storeInput = getByPlaceholderText('addProduct.storePlaceholder') as HTMLInputElement;
        fireEvent.change(storeInput, {target: {value: 'testStore'}});

        fireEvent.click(getByText('addProduct.finalizeProductCreation') as HTMLButtonElement);

        expect(getByText('addProduct.finalizeProductCreation'));
    });
})