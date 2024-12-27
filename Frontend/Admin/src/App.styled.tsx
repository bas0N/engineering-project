import { Text } from "@fluentui/react-components";
import styled from "styled-components";

export const AppWrapper = styled.main`
    min-height: 100vh !important;
    display: flex;
    flex-direction: column;
    align-items: center;
`;

export const AdminHeader = styled(Text).attrs({weight: 'semibold', size: 900, align: 'center'})`
    padding-top: 10px;
    padding-bottom: 32px;
`;