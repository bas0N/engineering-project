import { DataGrid, Text } from "@fluentui/react-components";
import styled from "styled-components";

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