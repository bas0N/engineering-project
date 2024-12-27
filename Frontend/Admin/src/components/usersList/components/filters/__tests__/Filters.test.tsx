import { render, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import { axe, toHaveNoViolations } from 'jest-axe';
import { Filters } from '../Filters';

expect.extend(toHaveNoViolations);

const MOCK_HANDLE_FILTER_CHANGE = jest.fn();
const MOCK_DELETE_MARKED_USERS = jest.fn();
const MOCK_FILTER_VALUE = 'loremIpsum';

describe('Users filters', () => {

    beforeEach(() => {
        jest.clearAllMocks();
    })

    it('Should have no a11y violations', async () => {
        const {container} = render(<Filters
            filter={MOCK_FILTER_VALUE} 
            handleFilterChange={MOCK_HANDLE_FILTER_CHANGE} 
            deleteMarkedUsers={MOCK_DELETE_MARKED_USERS} 
            deletingDisabled={false} 
        />);
        expect(await axe(container)).toHaveNoViolations();
    });

    it('Should be able to manipulate the filter input', () => {
        const {getByPlaceholderText} = render(<Filters
            filter={''} 
            handleFilterChange={MOCK_HANDLE_FILTER_CHANGE} 
            deleteMarkedUsers={MOCK_DELETE_MARKED_USERS} 
            deletingDisabled={false} 
        />);

        const filterInput = getByPlaceholderText('Enter filter value...') as HTMLInputElement;
        expect(filterInput);

        fireEvent.change(filterInput, {target: {value: MOCK_FILTER_VALUE}});
        expect(MOCK_HANDLE_FILTER_CHANGE).toHaveBeenCalledWith(MOCK_FILTER_VALUE);
    });

    it('Should be able to call the deleteMarkedUsers callback in case the button is enabled', () => {
        const {getByText} = render(<Filters
            filter={''} 
            handleFilterChange={MOCK_HANDLE_FILTER_CHANGE} 
            deleteMarkedUsers={MOCK_DELETE_MARKED_USERS} 
            deletingDisabled={false} 
        />);

        const deletingButton = getByText('Delete users') as HTMLButtonElement;
        expect(deletingButton).toBeEnabled();
        fireEvent.click(deletingButton);
        expect(MOCK_DELETE_MARKED_USERS).toHaveBeenCalled();
    });

    it('Should not be able to call the deleteMarkedUsers callback if the button is disabled', () => {
        const {getByText} = render(<Filters
            filter={''} 
            handleFilterChange={MOCK_HANDLE_FILTER_CHANGE} 
            deleteMarkedUsers={MOCK_DELETE_MARKED_USERS} 
            deletingDisabled={true} 
        />);

        const deletingButton = getByText('Delete users') as HTMLButtonElement;
        expect(deletingButton).toBeDisabled();
        fireEvent.click(deletingButton);
        expect(MOCK_DELETE_MARKED_USERS).not.toHaveBeenCalled();
    });
})