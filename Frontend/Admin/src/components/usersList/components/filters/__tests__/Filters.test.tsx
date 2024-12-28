import { render, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import { axe, toHaveNoViolations } from 'jest-axe';
import { Filters } from '../Filters';

expect.extend(toHaveNoViolations);

const MOCK_HANDLE_FILTER_CHANGE = jest.fn();
const MOCK_DELETE_MARKED_USERS = jest.fn();
const MOCK_CHANGE_USERS_ROLES = jest.fn();
const MOCK_SHOW_USERS_DETAILS = jest.fn();
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
            changeUsersRoles={MOCK_CHANGE_USERS_ROLES}
            triggerDetailsShowing={MOCK_SHOW_USERS_DETAILS}
            buttonsDisabled={false} 
        />);
        expect(await axe(container)).toHaveNoViolations();
    });

    it('Should be able to manipulate the filter input', () => {
        const {getByPlaceholderText} = render(<Filters
            filter={''} 
            handleFilterChange={MOCK_HANDLE_FILTER_CHANGE} 
            deleteMarkedUsers={MOCK_DELETE_MARKED_USERS} 
            changeUsersRoles={MOCK_CHANGE_USERS_ROLES}
            triggerDetailsShowing={MOCK_SHOW_USERS_DETAILS}
            buttonsDisabled={false} 
        />);

        const filterInput = getByPlaceholderText('Enter filter value...') as HTMLInputElement;
        expect(filterInput);

        fireEvent.change(filterInput, {target: {value: MOCK_FILTER_VALUE}});
        expect(MOCK_HANDLE_FILTER_CHANGE).toHaveBeenCalledWith(MOCK_FILTER_VALUE);
    });

    it('Should be able to use buttons in case they are enabled', () => {
        const {getByText} = render(<Filters
            filter={''} 
            handleFilterChange={MOCK_HANDLE_FILTER_CHANGE} 
            deleteMarkedUsers={MOCK_DELETE_MARKED_USERS} 
            changeUsersRoles={MOCK_CHANGE_USERS_ROLES}
            triggerDetailsShowing={MOCK_SHOW_USERS_DETAILS}
            buttonsDisabled={false} 
        />);

        const deletingButton = getByText('Delete users') as HTMLButtonElement;
        expect(deletingButton).toBeEnabled();
        fireEvent.click(deletingButton);
        expect(MOCK_DELETE_MARKED_USERS).toHaveBeenCalled();

        const changeRolesButton = getByText('Change roles') as HTMLButtonElement;
        expect(changeRolesButton).toBeEnabled();
        fireEvent.click(changeRolesButton);
        expect(MOCK_CHANGE_USERS_ROLES).toHaveBeenCalled();

        const triggerShowingButton = getByText('Show details') as HTMLButtonElement;
        expect(triggerShowingButton).toBeEnabled();
        fireEvent.click(triggerShowingButton);
        expect(MOCK_SHOW_USERS_DETAILS).toHaveBeenCalled();
    });

    it('Should not be able to use buttons in case they are disabled', () => {
        const {getByText} = render(<Filters
            filter={''} 
            handleFilterChange={MOCK_HANDLE_FILTER_CHANGE} 
            deleteMarkedUsers={MOCK_DELETE_MARKED_USERS} 
            changeUsersRoles={MOCK_CHANGE_USERS_ROLES}
            triggerDetailsShowing={MOCK_SHOW_USERS_DETAILS}
            buttonsDisabled={true} 
        />);

        const deletingButton = getByText('Delete users') as HTMLButtonElement;
        expect(deletingButton).toBeDisabled();
        fireEvent.click(deletingButton);
        expect(MOCK_DELETE_MARKED_USERS).not.toHaveBeenCalled();

        const changeRolesButton = getByText('Change roles') as HTMLButtonElement;
        expect(changeRolesButton).toBeDisabled();
        fireEvent.click(changeRolesButton);
        expect(MOCK_CHANGE_USERS_ROLES).not.toHaveBeenCalled();

        const triggerShowingButton = getByText('Show details') as HTMLButtonElement;
        expect(triggerShowingButton).toBeDisabled();
        fireEvent.click(triggerShowingButton);
        expect(MOCK_SHOW_USERS_DETAILS).not.toHaveBeenCalled();
    });
})