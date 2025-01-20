import { Link, Toast, ToastTitle, useToastController } from '@fluentui/react-components';
import { Cart24Regular } from '@fluentui/react-icons';
import { NavbarContainer, BasketButton, BasketButtonBadge, NavbarButton } from "./Navbar.styled"
import { Search } from "./search/Search"
import { useAuth } from 'authComponents/AuthProvider';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { useCallback, useState, useEffect } from 'react';

export const Navbar = () => {

    const {token, logout} = useAuth();
    const {t} = useTranslation();
    const navigate = useNavigate();
    const [productNumber, setProductNumber] = useState(0);
    const {dispatchToast} = useToastController('mainToaster');

    const handleLogout = () => {
        logout();  
        navigate('/signin');
    };
    const getBasketData = useCallback(async() => {
        try {
            const result = await axios.get(`${import.meta.env.VITE_API_URL}basket`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
            });
            setProductNumber(result.data.basketProducts.map((elem: {quantity: number}) => elem.quantity).reduce((partialSum: number, a: number) => partialSum + a, 0))
        } catch {
            dispatchToast(<Toast>
                <ToastTitle>{t('navbar.failedToLoadBasket')}</ToastTitle>
            </Toast>, {intent: 'error', position: 'top-end'})
        }
    }, [dispatchToast, t, token]);

    useEffect(() => {
        getBasketData();
    }, [getBasketData]);

    useEffect(() => {
        window.addEventListener("reloadBasketNumber", getBasketData);

        return () => {
            window.removeEventListener("reloadBasketNumber", getBasketData)
        }
    },[getBasketData]);

    return (
        <NavbarContainer>
            <Search />
            <Link href='/basket'>
                <BasketButton>
                    <Cart24Regular />
                    <BasketButtonBadge appearance='ghost' size='extra-large'>
                        {productNumber}
                    </BasketButtonBadge>
                </BasketButton>
            </Link>
            {token !== null ? (
                <>
                    <Link href='/settings'>
                        <NavbarButton appearance='subtle'>
                            {t('navbar.settingsButton')}
                        </NavbarButton>
                    </Link>
                    <NavbarButton onClick={handleLogout} appearance='subtle'>
                        {t('navbar.logoutButton')}
                    </NavbarButton>
                </>) : (<Link href='/signin'>
                <NavbarButton appearance='subtle'>
                    {t('navbar.signInButton')}
                </NavbarButton>
            </Link>)}
        </NavbarContainer>
    )
}