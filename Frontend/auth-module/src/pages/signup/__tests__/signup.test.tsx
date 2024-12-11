import { SignUpPanel } from "..";
import { fireEvent, render, waitFor } from '@testing-library/react';
import { axe, toHaveNoViolations } from 'jest-axe';
import { AuthProvider } from "../../../contexts/authContext";
import axios from "axios";

expect.extend(toHaveNoViolations);
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

const MOCK_TOKEN = 'mockToken';
const MOCK_REFRESH_TOKEN = 'mockRefreshToken';

const MOCK_RESOLVED_VALUE = {
    data: {
        token: MOCK_TOKEN, 
        refreshToken: MOCK_REFRESH_TOKEN
    }
}

jest.mock("react-router-dom", () => ({
    ...jest.requireActual("react-router-dom"), 
    Navigate: jest.fn(({ to }) => {
        return <div>Navigated to: {to}</div>;
      }),
}));

describe('Sign up panel' , () => {    
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    const MOCK_SET_ITEM = jest.fn((_key, _value) => {});
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    const MOCK_REMOVE_ITEM = jest.fn((_key) => {});
    
    beforeEach(() => {
        jest.clearAllMocks();
        Object.defineProperty(window, 'localStorage', {
            value: {
              getItem: jest.fn((key) => mockStorage[key] || null),
              setItem: MOCK_SET_ITEM,
              removeItem: MOCK_REMOVE_ITEM,
              clear: jest.fn(() => {
                mockStorage = {};
              }),
            },
            writable: true,
        });
        let mockStorage: Record<string, string> = {
        };
    });

    it('causes no a11y violations', async () => {
        const {container} = render(<AuthProvider>
            <SignUpPanel />
        </AuthProvider>);
        const results = await axe(container);
        expect(results).toHaveNoViolations();
    });

    it('Should display all of the stuff', () => {
        mockedAxios.post.mockResolvedValue(MOCK_RESOLVED_VALUE);
        const {getByText} = render(<AuthProvider>
            <SignUpPanel />
        </AuthProvider>);

        expect(getByText('authCard.signUpHeader'));
        expect(getByText('authCard.signUpButton'));
    });

    it('Should not render in case the token is present', () => {
        Object.defineProperty(window, 'localStorage', {
            value: {
              getItem: jest.fn((key) => mockStorage[key] || null),
              setItem: MOCK_SET_ITEM,
              removeItem: MOCK_REMOVE_ITEM,
              clear: jest.fn(() => {
                mockStorage = {};
              }),
            },
            writable: true,
        });
        let mockStorage: Record<string, string> = {
            "authToken": MOCK_TOKEN 
        };

        mockedAxios.post.mockResolvedValue(MOCK_RESOLVED_VALUE);
        const {findByText} = render(<AuthProvider>
            <SignUpPanel />
        </AuthProvider>);
        
        expect(MOCK_SET_ITEM).toHaveBeenCalled();
        expect(findByText(`Navigated to: /`)).toBeTruthy();
    });

    it('Should be able to sign the user in', async () => {
        mockedAxios.post.mockResolvedValue(MOCK_RESOLVED_VALUE);
        const {getByText, getByPlaceholderText} = render(<AuthProvider>
            <SignUpPanel />
        </AuthProvider>);

        const emailInput = getByPlaceholderText('authCard.email...');
        const passwordInput = getByPlaceholderText('authCard.password...');
        const passwordRepInput = getByPlaceholderText('authCard.passwordRep...');

        expect(emailInput);
        expect(passwordInput);
        expect(passwordRepInput)

        fireEvent.change(emailInput, {target: {value: 'test@test.pl'}});
        fireEvent.change(passwordInput, {target: {value: 'qwerty123'}});
        fireEvent.change(passwordInput, {target: {value: 'qwerty123'}});

        expect(getByText('authCard.signUpButton'));

        fireEvent.click(getByText('authCard.signUpButton'));

        await waitFor(() => {
            expect(MOCK_SET_ITEM).toHaveBeenCalled();
        })

    });

    it('Should display the error value in case the axios query fails', async () => {
        mockedAxios.post.mockRejectedValueOnce({data: {error: 'failed'}});
        const {getByText, getByPlaceholderText, findByText} = render(<AuthProvider>
            <SignUpPanel />
        </AuthProvider>);

        const emailInput = getByPlaceholderText('authCard.email...');
        const passwordInput = getByPlaceholderText('authCard.password...');
        const passwordRepInput = getByPlaceholderText('authCard.passwordRep...');

        expect(emailInput);
        expect(passwordInput);
        expect(passwordRepInput)

        fireEvent.change(emailInput, {target: {value: 'test@test.pl'}});
        fireEvent.change(passwordInput, {target: {value: 'qwerty123'}});
        fireEvent.change(passwordRepInput, {target: {value: 'qwerty123'}});

        expect(getByText('authCard.signUpButton'));

        fireEvent.click(getByText('authCard.signUpButton'));

        await waitFor(() => {
            expect(findByText('authCard.failureMessage'));
        })
    });

});