import { render, waitFor, fireEvent } from '@testing-library/react';
import axios from 'axios';
import { axe, toHaveNoViolations } from 'jest-axe';
import { User, nullableStringsComparator , modifyTableText } from "../UsersList.helper";
import { UsersList } from '../UsersList';
import { UserDetailsProps } from '../components/userDetails/UserDetails';
import { FiltersProps } from '../components/filters/Filters';

expect.extend(toHaveNoViolations);

const MOCK_USER_1:User = {
    id: 1,
    uuid: "uuid1",
    email: "",
    imageUrl: null,
    firstName: "firstName",
    lastName: null,
    role: "ADMIN",
    displayScore: 0
};

const MOCK_USER_2:User = {
    id: 2,
    uuid: "uuid2",
    email: "",
    imageUrl: null,
    firstName: null,
    lastName: null,
    role: "USER",
    displayScore: 0
};

jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

jest.mock('../components/filters/Filters', () => ({
    ...jest.requireActual('../components/filters/Filters'),
    Filters: (props: FiltersProps) => (<>
        <button onClick={() => props.handleFilterChange('firstName')}>TEST FILTER CHANGE</button>
        <button onClick={() => props.handleFilterChange('')}>TEST CLEAR FILTER CHANGE</button>
        <button onClick={() => props.triggerDetailsShowing()}>TEST DETAILS SHOWING</button>
        <button onClick={() => props.deleteMarkedUsers()}>TEST DELETE USERS</button>
    </>)
}))

jest.mock('../components/userDetails/UserDetails', () => ({
    ...jest.requireActual('../components/userDetails/UserDetails'),
    UserDetails: (props: UserDetailsProps) => (<>
        <div>{JSON.stringify(props.users)}</div>
        <button onClick={() => props.closeUserDetails()}>
            CLOSE USER DETAILS
        </button>
    </>)
}));

describe('Users List', () => {
    describe('Helper functions', () => {
        describe('modifyTableText', () => {
            it('Should truncate the text in case it is too long', () => {
                expect(modifyTableText('LoremIpsum', 5)).toBe('Lorem...');
            });
            it('Should leave the text as it is in case no need of truncating present', () => {
                expect(modifyTableText('LoremIpsum', 15)).toBe('LoremIpsum')
            })
        })
        describe('nullableStringsComparator', () => {
            it('Should return the general comparison between strings in case both of the properties are not null', () => {
                expect(nullableStringsComparator(MOCK_USER_1, MOCK_USER_1, 'firstName')).toBe((MOCK_USER_1.firstName as string).localeCompare((MOCK_USER_1.firstName as string)))
            });
            it('Should return -1 if the first property is null and the second is not' , () => {
                expect(nullableStringsComparator(MOCK_USER_2, MOCK_USER_1, 'firstName')).toEqual(-1);
            });

            it('Should return 1 if the first property is not null and the second is', () => {
                expect(nullableStringsComparator(MOCK_USER_1, MOCK_USER_2, 'firstName')).toEqual(1);
            });

            it('Should return 0 in case both properties are null', () => {
                expect(nullableStringsComparator(MOCK_USER_2, MOCK_USER_2, 'firstName')).toEqual(0);
            });
        })
    });
    describe('Component', () => {

        beforeEach(() => {
            jest.clearAllMocks();
            Object.defineProperty(window, 'localStorage', {
                value: {
                    getItem: jest.fn((key) => mockStorage[key] || null),
                    // eslint-disable-next-line @typescript-eslint/no-unused-vars
                    setItem: jest.fn((_key, _value) => {}),
                    // eslint-disable-next-line @typescript-eslint/no-unused-vars
                    removeItem: jest.fn((_key) => {}),
                    clear: jest.fn(() => {
                        mockStorage = {};
                    }),
                },
                writable: true,
            });
            let mockStorage: Record<string, string> = {
                token: 'testToken'
            };
        })

        it('Should display the loading spinner', () => {
            const {getByText} = render(<UsersList />);
            expect(getByText('Loading...'));
        });

        it('Should be able to handle the loading error', async () => {
            mockedAxios.get.mockRejectedValueOnce(new Error('network failure'));
            const {findByText} = render(<UsersList />);
            expect(await findByText('Something went wrong. Try later'));
        });

        it('Should not call the server in case no token is present', () => {
            Object.defineProperty(window, 'localStorage', {
                value: {
                    getItem: jest.fn((key) => mockStorage[key] || null),
                    // eslint-disable-next-line @typescript-eslint/no-unused-vars
                    setItem: jest.fn((_key, _value) => {}),
                    // eslint-disable-next-line @typescript-eslint/no-unused-vars
                    removeItem: jest.fn((_key) => {}),
                    clear: jest.fn(() => {
                        mockStorage = {};
                    }),
                },
                writable: true,
            });
            let mockStorage: Record<string, string> = {};
            mockedAxios.get.mockResolvedValueOnce({
                data: {
                    content: [MOCK_USER_1, MOCK_USER_2]
                }
            });
            render(<UsersList />);
            expect(mockedAxios.get).not.toHaveBeenCalled();
        });

        it('Should display the users table and have no a11y violations', async() => {
            mockedAxios.get.mockResolvedValueOnce({
                data: {
                    content: [MOCK_USER_1, MOCK_USER_2]
                }
            });

            const {container, getByText} = render(<UsersList />);
            waitFor(async() => {
                expect(getByText('TEST FILTER CHANGE'));
                expect(await axe(container)).toHaveNoViolations();
            });
        });

        it('Should display the width info in case the device is not large enough', async () => {
            mockedAxios.get.mockResolvedValueOnce({
                data: {
                    content: [MOCK_USER_1, MOCK_USER_2]
                }
            });
            Object.defineProperty(window, 'innerHeight', {
                writable: true,
                configurable: true,
                value: 150,
            });
        

            const {findByText} = render(<UsersList />);
            window.dispatchEvent(new Event('resize'));
            expect(await findByText('You need to have the device having the screen\'s with of at least 768px to be capable of handling the admin panel'))
        });

        it('Should be able to handle the user filtering process', async() => {
            mockedAxios.get.mockResolvedValueOnce({
                data: {
                    content: [MOCK_USER_1, MOCK_USER_2]
                }
            });
            const {findByText, findAllByRole, findAllByText, queryByText} = render(<UsersList />);
            expect(await findByText('USER'));

            fireEvent.click((await findAllByText('User Id') as HTMLElement[])[1]);
            fireEvent.click((await findAllByText('First Name') as HTMLElement[])[1]);
            fireEvent.click((await findAllByText('Last Name') as HTMLElement[])[1]);
            fireEvent.click((await findAllByText('Email') as HTMLElement[])[1]);
            fireEvent.click((await findAllByText('Role') as HTMLElement[])[1]);

            const filterTestButton = ((await findAllByRole('button', {name: 'TEST FILTER CHANGE'})) as HTMLButtonElement[])[1];
            expect(filterTestButton);

            fireEvent.click(filterTestButton);
            waitFor(() => {
                expect(queryByText('USER')).toBeFalsy();
            })
    
            const clearFilterTestButton = ((await findAllByRole('button', {name: 'TEST CLEAR FILTER CHANGE'})) as HTMLButtonElement[])[1];
            expect(clearFilterTestButton);

            fireEvent.click(clearFilterTestButton);
            waitFor(() => {
                expect(queryByText('USER')).toBeTruthy();
            })
        });


        it('Should be able to inspect user details', async() => {
            mockedAxios.get.mockResolvedValueOnce({
                data: {
                    content: [MOCK_USER_1, MOCK_USER_2]
                }
            });
            const {findByText, findAllByRole, queryAllByLabelText} = render(<UsersList />);
            expect(await findByText('USER'));
            expect(queryAllByLabelText('Select row').length).toEqual(4);
            const userSelection = queryAllByLabelText('Select row')[3] as HTMLElement;
            fireEvent.click(userSelection);

            fireEvent.click((await findAllByRole('button', {name: 'TEST DETAILS SHOWING'}) as HTMLButtonElement[])[1]);
            const closeDetailsButton = await findByText('CLOSE USER DETAILS') as HTMLButtonElement;
            expect(closeDetailsButton);
            fireEvent.click(closeDetailsButton);
        });

        it('Should be able to handle the user deletion', async() => {
            mockedAxios.get.mockResolvedValueOnce({
                data: {
                    content: [MOCK_USER_1, MOCK_USER_2]
                }
            });
            mockedAxios.delete.mockResolvedValueOnce({
                data: ''
            })
            const {findByText, queryAllByLabelText, findAllByRole} = render(<UsersList />);
            expect(await findByText('USER'));
            expect(queryAllByLabelText('Select row').length).toEqual(4);
            const userSelection = queryAllByLabelText('Select row')[3] as HTMLElement;
            fireEvent.click(userSelection);

            const deleteMarkedUsersButton = ((await findAllByRole('button', {name: 'TEST DELETE USERS'})) as HTMLButtonElement[])[1];
            expect(deleteMarkedUsersButton);

            fireEvent.click(deleteMarkedUsersButton);

            expect(mockedAxios.delete).toHaveBeenCalled();


            mockedAxios.delete.mockRejectedValueOnce(new Error('network failure'));

            fireEvent.click(deleteMarkedUsersButton);

            expect(mockedAxios.delete).toHaveBeenCalledTimes(2);
        });
    })
})