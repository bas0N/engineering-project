import { createContext, ReactNode, useContext, useEffect, useState } from 'react';

// Define the shape of the context state
interface AuthContextType {
  token: string | null;
  setToken: (token: string | null) => void;
  login: (token: string) => void;
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
    //const router = useHistory();

    // Save token to localStorage and state
    const login = (newToken: string) => {
        localStorage.setItem('authToken', newToken);
        setToken(newToken);
    };

    // Remove token from localStorage and state
    const logout = () => {
        localStorage.removeItem('authToken');
        setToken(null);
        //router.push('/login');
    };

    useEffect(() => {
    // Check for token in localStorage on initial load
        const savedToken = localStorage.getItem('authToken');
        if (savedToken) {
            setToken(savedToken);
        }
    }, []);

    return (
        <AuthContext.Provider value={{ token, setToken, login, logout }}>
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
