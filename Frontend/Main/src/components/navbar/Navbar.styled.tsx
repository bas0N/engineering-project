import { Badge, Button } from '@fluentui/react-components';
import styled from "styled-components";

export const NavbarContainer = styled.nav`
    width: calc(100% - 10px);
    padding: 0px 5px;
    display: flex;
    flex-direction: row;
    gap: 8px;
    justify-content: space-between;
    align-items: center;
`;

export const BasketButton = styled(Button).attrs({appearance: 'transparent'})`
    padding: 10px 20px;
    border-radius: 10px;
    color: white;
    text-decoration: none;
`;

export const BasketButtonBadge = styled(Badge).attrs({appearance: 'ghost', size: 'extra-large'})`
    font-size: 16px;
    color: inherit;
`;

export const LogoutButton = styled(Button).attrs({appearance: 'transparent'})`
    color: white;
    padding: 10px 20px;
    text-decoration: none;
`;