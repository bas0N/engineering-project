import { render, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { axe, toHaveNoViolations } from 'jest-axe';
import { LoginPanel } from '../LoginPanel';
import axios from "axios";

expect.extend(toHaveNoViolations);

jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

const MOCK_TOKEN = 'loremIpsum';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
const MOCK_SET_ITEM = jest.fn((_key, _value) => {})

describe('Login Panel', () => {

    beforeEach(() => {
        jest.clearAllMocks();
        Object.defineProperty(window, 'localStorage', {
            value: {
                getItem: jest.fn((key) => mockStorage[key] || null),
                setItem: MOCK_SET_ITEM,
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
        const {container} = render(<LoginPanel />);
        expect(await axe(container)).toHaveNoViolations();
    });

    it('Should be able to log the user in', () => {
        mockedAxios.post.mockResolvedValueOnce({
            data: {
                token: MOCK_TOKEN
            }
        });

        const {getByPlaceholderText, getByText} = render(<LoginPanel />);
        const emailInput = getByPlaceholderText('Email...') as HTMLInputElement;
        const passwordInput = getByPlaceholderText('Password...') as HTMLInputElement;
        const submitButton = getByText('Sign in') as HTMLButtonElement;
        expect(emailInput);
        expect(passwordInput);
        expect(submitButton).toBeDisabled();
        fireEvent.change(emailInput, {target: {value: 'a@b.pl'}});
        fireEvent.change(passwordInput, {target: {value: 'qwerty123'}});
        expect(submitButton).toBeEnabled();
        fireEvent.click(submitButton);

        waitFor(() => {
            expect(MOCK_SET_ITEM).toHaveBeenCalledWith('token', MOCK_TOKEN);
        })
    });

    it('Should handle the network error during the log in process', () => {
        mockedAxios.post.mockRejectedValueOnce(new Error('network failure'));

        const {getByPlaceholderText, getByText, findByText} = render(<LoginPanel />);
        const emailInput = getByPlaceholderText('Email...') as HTMLInputElement;
        const passwordInput = getByPlaceholderText('Password...') as HTMLInputElement;
        const submitButton = getByText('Sign in') as HTMLButtonElement;
        expect(emailInput);
        expect(passwordInput);
        expect(submitButton).toBeDisabled();
        fireEvent.change(emailInput, {target: {value: 'a@b.pl'}});
        fireEvent.change(passwordInput, {target: {value: 'qwerty123'}});
        expect(submitButton).toBeEnabled();
        fireEvent.click(submitButton);

        waitFor(() => {
            expect(findByText('Login error'));
        });
    });


})