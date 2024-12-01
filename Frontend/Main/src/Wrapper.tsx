import { ReactNode, useCallback, useEffect } from "react"
import { useNavigate, useParams } from "react-router";

interface WrapperProps {
    children: JSX.Element[] | ReactNode[];
}

export const PageWrapper = ({children}:WrapperProps) => {

    const navigate = useNavigate();
    const params = useParams();
    console.log(params);

    const handleStorageRedirect = useCallback(() => {
        const data = localStorage.getItem('redirect');
        if(data !== null){
            navigate(data);
        }
    }, [navigate])

    useEffect(() => {
        window.addEventListener("redirect", handleStorageRedirect);

        return () => {
            window.removeEventListener("storage", handleStorageRedirect)
        }
    },[handleStorageRedirect]);

    return children
}