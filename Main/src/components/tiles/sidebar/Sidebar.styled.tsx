import { Button, Combobox, Input, Text } from '@fluentui/react-components';
import styled from 'styled-components';

export const SidebarWrapper = styled.section<{isOpened: boolean}>`
    width: calc(98vw - 20px);
    height: calc(80vh - 20px);
    overflow-y: scroll;
    padding: 10px;
    border-radius: 10px;
    background: #222222;
    position: fixed;
    top: 130px;
    left: ${(props) => props.isOpened ? '1vw' :  'calc( -100vw + 20px - 10px - 12px)'};
    z-index: 2;
    transition: left 0.4s;

    @media screen and (min-width: 425px){
        position: fixed;
        top: 130px;
        left: ${(props) => props.isOpened ? '0' :  'calc( -100vw + 20px - 10px - 12px)'};
    }

    @media screen and (min-width: 1440px){
        width: calc(20vw - 20px);
        position: initial !important;
        top: 0;
        left: 0;
        background: #222222a0;
    }
`;

export const SidebarClosingButton = styled(Button).attrs({appearance: 'transparent'})`
    position: absolute;
    top: 10px;
    right: 10px;
    height: fit-content;
    width: fit-content;
    padding: 0px;
    z-index: 3;

    @media screen and (min-width: 1440px){
        display: none;
    }
`;

export const SidebarHeader = styled(Text).attrs({as: 'h2', size: 500, align: 'center'})`
    width: 100%;
    padding-bottom: 20px;
    padding-top: 10px;
`;

export const SidebarCategoryWrapper = styled.div`
    width: calc(100% - 10px);
    padding: 10px 5px;
    display: flex;
    flex-direction: column;
    gap: 4px;
`;

export const SidebarCategoryDropdownWrapper = styled.div`
    max-width: 100% !important;
    width: 100%;
`;

export const SidebarCategoryDropdown = styled(Combobox)`
    width: 100%;
    min-width: 100% !important;

    & > * {
        min-width: 100% !important;
    }
`;

export const SidebarCategoryHeader = styled(Text).attrs({as: 'h3', size: 400, align: 'center'})`
    width: 100%;
    padding-bottom: 8px;
`;

export const SidebarPriceContainer = styled.div`
    width: calc(100% - 10px);
    padding: 5px;
    display: flex;
    flex-direction: row;
    justify-content: space-around;
    align-items: center;
`;

export const SidebarPriceInput = styled(Input).attrs({type: 'number', min: '0'})`
    max-width: 45%;
    flex: 1;
`;