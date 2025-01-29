import { render } from '@testing-library/react';
import { axe, toHaveNoViolations } from 'jest-axe';
import App from "./App";

expect.extend(toHaveNoViolations);

jest.mock('./components/loginPanel/LoginPanel', () => ({
    LoginPanel: () => (<>LOGIN PANEL</>)
}));

jest.mock('./components/usersList/UsersList', () => ({
    UsersList: () => (<>USERS LIST</>)
}))

describe('Admin', () => {
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
        let mockStorage: Record<string, string> = {};
    });
    it('Should have no a11y violations', async() => {
        const {container} = render(<App />);
        expect(await axe(container)).toHaveNoViolations();
    });

    it('Should display the login panel in case no token present', () => {
        const {getByText} = render(<App />);
        expect(getByText('LOGIN PANEL'));
    });

    it('Should display the users list in case the token is provided', () => {
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
            token: 'loremIpsum'
        };
        const {getByText} = render(<App />);
        expect(getByText('USERS LIST'));
    });
})