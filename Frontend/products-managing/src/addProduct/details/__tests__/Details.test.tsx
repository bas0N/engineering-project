import { axe, toHaveNoViolations } from "jest-axe";
import { render, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import { Details } from '../Details'

expect.extend(toHaveNoViolations);

const MOCK_DETAILS:Record<string,string> = {
    'DEPARTMENT': 'detailValue'
};

const MOCK_SET_DETAILS = jest.fn();

describe('Details', () => {

    beforeEach(() => {
        jest.clearAllMocks();
    })

    it('Should have no a11y violations', async() => {
        const {container} = render(<Details 
            details={MOCK_DETAILS}
            setDetails={MOCK_SET_DETAILS}
        />);

        expect(await axe(container)).toHaveNoViolations();
    });

    it('Should be able to add a new detail', async() => {
        const {getByPlaceholderText, getByLabelText, findByRole} = render(<Details 
            details={{} as Record<string,string>}
            setDetails={MOCK_SET_DETAILS}
        />);

        const nameDropdown = getByLabelText('addProduct.details.detailNamePlaceholder') as HTMLButtonElement;
        const valueInput = getByPlaceholderText('addProduct.details.detailValuePlaceholder') as HTMLInputElement;
        const addButton = getByLabelText('addProduct.details.addDetailLabel') as HTMLButtonElement;
        expect(addButton);
        expect(nameDropdown);
        expect(valueInput);

        fireEvent.click(addButton);
        expect(MOCK_SET_DETAILS).not.toHaveBeenCalled();

        fireEvent.click(nameDropdown);
        expect(await findByRole('option', {name: 'Department'}));
        fireEvent.click(await findByRole('option', {name: 'Department'}) as HTMLOptionElement);
        fireEvent.click(addButton);
        expect(MOCK_SET_DETAILS).not.toHaveBeenCalled();

        fireEvent.change(valueInput, {target: {value: 'detailValue1'}});
        fireEvent.click(addButton);
        expect(MOCK_SET_DETAILS).toHaveBeenCalledWith({
            ...MOCK_DETAILS,
            'DEPARTMENT': 'detailValue1'
        })
    });

    it('Should be able to modify the existing detail', () => {
        const {getByPlaceholderText, getByLabelText} = render(<Details 
            details={MOCK_DETAILS}
            setDetails={MOCK_SET_DETAILS}
        />);

        const nameDropdown = getByLabelText('addProduct.details.detailNamePlaceholder') as HTMLInputElement;
        const valueInput = getByPlaceholderText('addProduct.details.detailValuePlaceholder') as HTMLInputElement;
        expect(nameDropdown);
        expect(valueInput);

        const editButton = getByLabelText('addProduct.details.editLabel') as HTMLButtonElement;
        expect(editButton);
        fireEvent.click(editButton);

        expect(MOCK_SET_DETAILS).toHaveBeenCalledWith({});
        expect(nameDropdown.value).toEqual('Department');
        expect(valueInput.value).toEqual('detailValue');
    });

    it('Should be able to delete the existing detail', () => {
        const {getByLabelText} = render(<Details 
            details={MOCK_DETAILS}
            setDetails={MOCK_SET_DETAILS}
        />);

        const deleteButton = getByLabelText('addProduct.details.deleteLabel') as HTMLButtonElement;
        expect(deleteButton);

        fireEvent.click(deleteButton);
        expect(MOCK_SET_DETAILS).toHaveBeenCalledWith({})
    });
})