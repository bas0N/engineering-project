import { axe, toHaveNoViolations } from "jest-axe";
import { render, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import axios from 'axios';
import { AddProductImage } from "../AddProductImage"

expect.extend(toHaveNoViolations);

jest.mock('axios');

const MOCK_CLOSE_PANEL = jest.fn();
const MOCK_PRODUCT_ID = 'productId';
const MOCK_TOASTER_ID = 'toasterId';

const mockedAxios = axios as jest.Mocked<typeof axios>;

describe('Adding Product', () => {

    beforeEach(() => {
        jest.clearAllMocks();
    })

    it('Should have no a11y violations', async() => {
        const {container} = render(<AddProductImage 
            productId={MOCK_PRODUCT_ID}
            closePanel={MOCK_CLOSE_PANEL}
            toasterId={MOCK_TOASTER_ID}
        />);
        expect(await axe(container)).toHaveNoViolations();
    });

    it('Should be able to upload the image', async() => {
        mockedAxios.post.mockResolvedValueOnce({
            data: {}
        })
        const {getByText, getByLabelText} = render(<AddProductImage 
            productId={MOCK_PRODUCT_ID}
            closePanel={MOCK_CLOSE_PANEL}
            toasterId={MOCK_TOASTER_ID}
        />);

        fireEvent.click(getByText('productImageManagement.addingProduct.load') as HTMLButtonElement);

        const file = new File([''], 'testFile.png', { type: 'image/png' });
        const imageInput = getByLabelText('productImageManagement.addingProduct.imageInput') as HTMLInputElement;

        fireEvent.change(imageInput, {
            target: { files: [file] },
        });

        expect(getByText('productImageManagement.addingProduct.productLoaded'));

        fireEvent.click(getByText('productImageManagement.addingProduct.upload') as HTMLButtonElement);
        await waitFor(() => {
            expect(MOCK_CLOSE_PANEL).toHaveBeenCalled(); 
        });
    });

    it('Should not be able to upload no data', () => {
        const {getByText, getByLabelText} = render(<AddProductImage 
            productId={MOCK_PRODUCT_ID}
            closePanel={MOCK_CLOSE_PANEL}
            toasterId={MOCK_TOASTER_ID}
        />);

        fireEvent.click(getByText('productImageManagement.addingProduct.load') as HTMLButtonElement);

        const imageInput = getByLabelText('productImageManagement.addingProduct.imageInput') as HTMLInputElement;

        fireEvent.change(imageInput, {
            target: { files: null },
        });

        expect(getByText('productImageManagement.addingProduct.loadProduct'));

    })

    it('Should be able to close the panel', () => {
        const {getByLabelText} = render(<AddProductImage 
            productId={MOCK_PRODUCT_ID}
            closePanel={MOCK_CLOSE_PANEL}
            toasterId={MOCK_TOASTER_ID}
        />);

        fireEvent.click(getByLabelText('productImageManagement.addingProduct.closeButton') as HTMLButtonElement);

        expect(MOCK_CLOSE_PANEL).toHaveBeenCalled();
    });

    it('Should be able to handle the loading error', async() => {
        mockedAxios.post.mockRejectedValueOnce(new Error('network failure'));
        const {getByText, getByLabelText} = render(<AddProductImage 
            productId={MOCK_PRODUCT_ID}
            closePanel={MOCK_CLOSE_PANEL}
            toasterId={MOCK_TOASTER_ID}
        />);

        fireEvent.click(getByText('productImageManagement.addingProduct.load') as HTMLButtonElement);

        const file = new File([''], 'testFile.png', { type: 'image/png' });
        const imageInput = getByLabelText('productImageManagement.addingProduct.imageInput') as HTMLInputElement;

        fireEvent.change(imageInput, {
            target: { files: [file] },
        });

        expect(getByText('productImageManagement.addingProduct.productLoaded'));

        fireEvent.click(getByText('productImageManagement.addingProduct.upload') as HTMLButtonElement);
        await waitFor(() => {
            expect(getByText('productImageManagement.addingProduct.productLoaded'));
        })
    });
});