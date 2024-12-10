import { fireEvent, render, renderHook } from "@testing-library/react";
import { AuthProvider, useAuth } from "../authContext";

const MOCK_AUTH_TOKEN = 'authToken';
const MOCK_REFRESH_TOKEN = 'refreshToken';

const MockUseAuth = () => {
    const {login, logout, token, refreshToken} = useAuth();

    return <>
        <button onClick={() => login(MOCK_AUTH_TOKEN, MOCK_REFRESH_TOKEN)}>login</button>
        <button onClick={() => logout()}>logout</button>
        <div>token: {token}</div>
        <div>refresh token: {refreshToken}</div>
    </>
}
describe('Testing Auth context', () => {
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
    
        let mockStorage: Record<string, string> = {};
      });
    it('Should be able to login', () => {
        const {getByText} = render(<AuthProvider>
                <MockUseAuth />
            </AuthProvider>);
        expect(getByText('login'));
        fireEvent.click(getByText('login'));
        expect(MOCK_SET_ITEM).toHaveBeenCalled();
        expect(getByText(`token: ${MOCK_AUTH_TOKEN}`));
        expect(getByText(`refresh token: ${MOCK_REFRESH_TOKEN}`));
    });
    it('Should be able to logout after login', () => {
        const {getByText} = render(<AuthProvider>
                <MockUseAuth />
            </AuthProvider>);
        expect(getByText('login'));
        fireEvent.click(getByText('login'));
        expect(getByText('logout'));
        fireEvent.click(getByText('logout'));
        expect(MOCK_REMOVE_ITEM).toHaveBeenCalled();
        expect(getByText(`token:`));
        expect(getByText(`refresh token:`));
    });
    it('Should be displaying the tokens in case the user is already signed in', () => {
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
            "authToken": MOCK_AUTH_TOKEN,
            "refreshToken": MOCK_REFRESH_TOKEN
        };
        const {getByText} = render(<AuthProvider>
            <MockUseAuth />
        </AuthProvider>);
        expect(getByText(`token: ${MOCK_AUTH_TOKEN}`));
        expect(getByText(`refresh token: ${MOCK_REFRESH_TOKEN}`));
    });

    it('Should throw an error in case no AuthProvider provided', () => {
        expect(() => {  
            renderHook(() => useAuth());  
        }).toThrow();  
    });
});