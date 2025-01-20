// jest.setup.ts

jest.mock('./authComponents/AuthProvider', () => ({
    useAuth: () => jest.fn()
}), {virtual: true});

