import { createContext, ReactNode, useContext, useEffect, useState } from 'react';

interface AuthContextType {
  token: string | null;
  refreshToken: string | null;
  setToken: (token: string | null) => void;
  login: (token: string, refreshToken: string) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider = ({ children }: AuthProviderProps) => {
    const [token, setToken] = useState<string | null>(null);
    const [refreshToken, setRefreshToken] = useState<string | null>(null);

    const login = (newToken: string, newRefreshToken: string) => {
        localStorage.setItem('authToken', newToken);
        localStorage.setItem('refreshToken', newRefreshToken);
        setToken(newToken);
        setRefreshToken(newRefreshToken);
    };

    const logout = () => {
        localStorage.removeItem('authToken');
        localStorage.removeItem('refreshToken');
        setToken(null);
        setRefreshToken(null);
    };

    useEffect(() => {
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
