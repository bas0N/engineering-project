import { createContext, ReactNode, useContext, useEffect, useState } from 'react';

// Define the shape of the context state
interface AuthContextType {
  token: string | null;
  refreshToken: string | null;
  setToken: (token: string | null) => void;
  login: (token: string, refreshToken: string) => void;
  logout: () => void;
}

// Create the context with a default value
const AuthContext = createContext<AuthContextType | undefined>(undefined);

// AuthProvider props
interface AuthProviderProps {
  children: ReactNode;
}

// AuthProvider Component
export const AuthProvider = ({ children }: AuthProviderProps) => {
    const [token, setToken] = useState<string | null>(null);
    const [refreshToken, setRefreshToken] = useState<string | null>(null);
    //const router = useHistory();

    // Save token to localStorage and state
    const login = (newToken: string, newRefreshToken: string) => {
        localStorage.setItem('authToken', newToken);
        localStorage.setItem('refreshToken', newRefreshToken);
        setToken(newToken);
        setRefreshToken(newRefreshToken);
    };

    // Remove token from localStorage and state
    const logout = () => {
        localStorage.removeItem('authToken');
        localStorage.removeItem('refreshToken');
        setToken(null);
        setRefreshToken(null);
        //router.push('/login');
    };

    useEffect(() => {
    // Check for token in localStorage on initial load
        const savedToken = localStorage.getItem('authToken');
        if (savedToken) {
            setToken(savedToken);
        }
        const savedRefreshToken = localStorage.getItem('refreshToken');
        if (savedRefreshToken) {
            setRefreshToken(savedRefreshToken);
        }
    }, []);

    return (
        <AuthContext.Provider value={{ token, refreshToken, setToken, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

// eslint-disable-next-line react-refresh/only-export-components
export const useAuth = () => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};
