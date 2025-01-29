import styled from "styled-components";
import { Text } from "@fluentui/react-components";

export const RolesWrapper = styled.section`
    padding: 10px;
    width: calc(100vw - 20px);
    height: calc(100vh - 20px);
    position: fixed;
    top: 0;
    left: 0;
    background: rgba(0,0,0,.9);
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    z-index: 2;
`;

export const RolesHeader = styled(Text).attrs({size: 900, weight: 'semibold', align: 'center'})`
    padding: 10px 0px;
`;

export const RolesTableContainer = styled.div`
    width: calc(70% - 20px);
    padding: 10px;
    padding-bottom: 30px;
    display: flex;
    flex-direction: column;
    gap: 8px;
    align-items: center;
`;

export const RoleWrapper = styled.div`
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 8px;
`;


export const RoleEmail = styled(Text).attrs({align: 'start', size: 400})`
    flex: 1;
`;

export const RoleButtonsWrapper = styled.div`
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 8px;
`;