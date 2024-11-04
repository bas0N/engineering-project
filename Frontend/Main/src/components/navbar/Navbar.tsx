import { Link } from '@fluentui/react-components';
import { Cart24Regular } from '@fluentui/react-icons';
import { NavbarContainer, BasketButton, BasketButtonBadge } from "./Navbar.styled"
import { Search } from "./search/Search"

export const Navbar = () => {
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
        </NavbarContainer>
    )
}