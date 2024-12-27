import { DataGrid, Input, Text } from "@fluentui/react-components";
import styled from "styled-components";

export const UsersListControlsWrapper = styled.section`
    width: calc(40% - 20px);
    padding: 10px;
    display: none;
    flex-direction: column;
    align-items: center;
    gap: 16px;

    @media screen and (min-width: 768px){
        display: flex;
    }
`;

export const UsersFilterInput = styled(Input).attrs({appearance: 'underline'})`
    width: calc(100% - 20px);
    padding: 10px;
    text-indent: 20px;
`;

export const UsersFilterButtonsWrapper = styled.div`
    width: calc(100% - 20px);
    padding: 10px;
    display: flex;
    flex-direction: row;
    align-items: center;
    gap: 8px;
    justify-content: center;
`;

export const UsersListDataGrid = styled(DataGrid)`
    width: calc(100% - 20px);
    padding: 10px;
    display: none;

    @media screen and (min-width: 768px) {
        display: block;
    }

    @media screen and (min-width: 1024px){
        width: calc(80% - 20px);
    }
`;

export const UsersDisplayInappropriateInfo = styled(Text).attrs({size: 500, align: 'center'})`
    padding: 10px;

    @media screen and (min-width: 768px) {
        display: none;
    }
`;