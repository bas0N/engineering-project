import { render, fireEvent } from "@testing-library/react";
import { axe, toHaveNoViolations } from 'jest-axe' 
import axios from 'axios'
import { Roles } from "../Roles"
import { User } from "../../../UsersList.helper";

expect.extend(toHaveNoViolations);

const MOCK_CLOSE_ROLES_PANEL = jest.fn();

const MOCK_USERS:User[] = [{
    id: 1,
    uuid: "",
    email: "test@test.pl",
    imageUrl: null,
    firstName: null,
    lastName: null,
    role: "ADMIN",
    displayScore: 0
},{
    id: 2,
    uuid: "",
    email: "test2@test.pl",
    imageUrl: null,
    firstName: null,
    lastName: null,
    role: "USER",
    displayScore: 0
}];

jest.mock('axios');

const mockedAxios = axios as jest.Mocked<typeof axios>;

describe('Roles', () => {

    beforeEach(() => {
        jest.clearAllMocks();
    })

    it('Should have no a11y violations', async() => {
        const {container, getAllByText} = render(<Roles 
            users={MOCK_USERS} 
            closeRolesPanel={MOCK_CLOSE_ROLES_PANEL} 
        />);
        expect(await axe(container)).toHaveNoViolations();
        expect(getAllByText('Make an admin').length).toEqual(2);
    });

    it('Should be able to change the user roles', () => {
        mockedAxios.patch.mockResolvedValueOnce({
            data: {

            }
        })
        const {getAllByText} = render(<Roles 
            users={MOCK_USERS} 
            closeRolesPanel={MOCK_CLOSE_ROLES_PANEL} 
        />);

        const adminButtons = getAllByText('Make an admin') as HTMLButtonElement[];
        const userButtons = getAllByText('Make a user') as HTMLButtonElement[];
        
        fireEvent.click(adminButtons[1]);

        mockedAxios.patch.mockResolvedValueOnce({
            data: {

            }
        })
        fireEvent.click(userButtons[0]);
    });

    it('Should be able to handle the network failures', async() => {
        mockedAxios.patch.mockRejectedValueOnce(new Error('network failure'));
        const {findByText, getAllByText} = render(<Roles 
            users={MOCK_USERS} 
            closeRolesPanel={MOCK_CLOSE_ROLES_PANEL} 
        />);

        const adminButton = (getAllByText('Make an admin') as HTMLButtonElement[])[0];
        fireEvent.click(adminButton);

        expect(await findByText('Failed to update the role'));
    });

    it('Should be able to close the panel', () => {
        const {getByText} = render(<Roles 
            users={MOCK_USERS} 
            closeRolesPanel={MOCK_CLOSE_ROLES_PANEL} 
        />);

        const closeButton = getByText('Close panel') as HTMLButtonElement;
        fireEvent.click(closeButton);
        expect(MOCK_CLOSE_ROLES_PANEL).toHaveBeenCalled();
    })
})