import { useState } from "react";

export const useAuth = () => {

    const [token, setToken] = useState<string | null>('');

    return {
        token,
        logout: () => {
            setToken(null);
        }
    }
}