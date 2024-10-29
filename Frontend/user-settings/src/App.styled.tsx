import { Text } from "@fluentui/react-components";
import styled from "styled-components";

export const UserSettingsWrapper = styled.section`
    width: calc(100% - 20px);
    padding: 10px;
    text-align: center;
    display: flex;
    flex-direction: column;
    gap: 8px;
    align-items: center;
`;

export const UserSettingsHeader = styled(Text).attrs({as: 'h1', size: 900, weight: 'semibold', align: 'center'})`
    padding-bottom: 48px;
`;