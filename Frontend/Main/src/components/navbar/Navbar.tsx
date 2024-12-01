import { Link } from '@fluentui/react-components';
import { Cart24Regular } from '@fluentui/react-icons';
import { NavbarContainer, BasketButton, BasketButtonBadge, LogoutButton } from "./Navbar.styled"
import { Search } from "./search/Search"
import { useAuth } from 'authComponents/AuthProvider';
import { useTranslation } from '../../../node_modules/react-i18next';
import { useNavigate } from 'react-router-dom';

export const Navbar = () => {

    const {token, logout} = useAuth();
    const {t} = useTranslation();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();  
        return navigate('/signin');
    };

    return (
        <NavbarContainer>
            <Search />
            <Link href='/basket'>
                <BasketButton>
                    <Cart24Regular />
                    <BasketButtonBadge appearance='ghost' size='extra-large'>
                        0
                    </BasketButtonBadge>
                </BasketButton>
            </Link>
            {token !== null ? (<LogoutButton onClick={handleLogout} appearance='subtle'>
                {t('navbar.logoutButton')}
            </LogoutButton>) : (<Link href='/signin'>
                <LogoutButton appearance='subtle'>
                    {t('navbar.signInButton')}
                </LogoutButton>
            </Link>)}
        </NavbarContainer>
    )
}